package init.app.service;

import com.jcabi.aspects.Loggable;
import init.app.domain.model.Content;
import init.app.domain.model.ContentLike;
import init.app.domain.model.User;
import init.app.domain.model.enumeration.ContentType;
import init.app.domain.model.enumeration.NotificationType;
import init.app.domain.model.enumeration.ReactionType;
import init.app.domain.repository.ContentLikeRepository;
import init.app.domain.repository.ContentRepository;
import init.app.domain.repository.UserRepository;
import init.app.exception.CustomException;
import init.app.web.dto.custom.UpdateContentNotificationDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

@Service
@Loggable(trim = false, prepend = true)
@Transactional(rollbackFor = Exception.class)
public class ContentLikeService {

    @Inject
    UserService userService;

    @Inject
    UserRepository userRepository;

    @Inject
    @Lazy
    ContentService contentService;

    @Inject
    ContentRepository contentRepository;

    @Inject
    ContentLikeRepository contentLikeRepository;

    @Inject
    NotificationService notificationService;

    @Inject
    private SimpMessagingTemplate simpMessagingTemplate;

    public void create(Long principalId, ReactionType type, Long contentId) throws CustomException {

        Content content = contentService.getByRepoMethod(contentRepository.findById(contentId));

        User user = userService.getByRepoMethod(userRepository.findById(principalId));

        ContentLike existingLike = contentLikeRepository.findByUserAndContent(user, content);

        if (existingLike != null && !existingLike.getIsDeleted()) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("CONTENT_ALREADY_LIKED"));
        }

        ContentLike like;

        if (existingLike == null) {
            like = new ContentLike();
            like.setContent(content);
            like.setUser(user);
            like.setType(type.name());
            like.setCreateTime(ZonedDateTime.now());
        } else {
            like = existingLike;
        }

        like.setUpdateTime(ZonedDateTime.now());
        like.setIsDeleted(false);

        contentLikeRepository.save(like);

        notificationService.createUpdateNotification(content.getUser(), user, NotificationType.LIKED, content, null, null);

        if (content.getType().equals(ContentType.POST)) {
            UpdateContentNotificationDto updateContentNotificationDto = UpdateContentNotificationDto.builder().contentId(content.getId()).build();
            simpMessagingTemplate.convertAndSend("/content/update", updateContentNotificationDto);
        }
    }

    public void delete(Long principalId, Long contentId) throws CustomException {
        Content content = contentService.getByRepoMethod(contentRepository.findById(contentId));

        User user = userService.getByRepoMethod(userRepository.findById(principalId));

        ContentLike existingLike = contentLikeRepository.findByUserAndContent(user, content);

        if (existingLike == null || existingLike.getIsDeleted()) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("CONTENT_NOT_LIKED"));
        }

        deleteLike(existingLike);

        if (content.getType().equals(ContentType.POST)) {
            UpdateContentNotificationDto updateContentNotificationDto = UpdateContentNotificationDto.builder().contentId(contentId).build();
            simpMessagingTemplate.convertAndSend("/content/update", updateContentNotificationDto);
        }
    }

    public void deleteLike(ContentLike like) {
        like.setIsDeleted(true);
        like.setUpdateTime(ZonedDateTime.now());

        contentLikeRepository.save(like);
    }
}
