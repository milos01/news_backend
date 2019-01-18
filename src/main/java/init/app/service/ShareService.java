package init.app.service;

import com.jcabi.aspects.Loggable;
import init.app.domain.model.Content;
import init.app.domain.model.User;
import init.app.domain.model.enumeration.ContentType;
import init.app.domain.model.enumeration.NotificationType;
import init.app.domain.repository.ContentRepository;
import init.app.domain.repository.UserRepository;
import init.app.exception.CustomException;
import init.app.web.dto.custom.UpdateContentNotificationDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.ZonedDateTime;


@Service
@Loggable(trim = false, prepend = true)
@Transactional(rollbackFor = Exception.class)
public class ShareService {

    @Inject
    NotificationService notificationService;
    @Inject
    ContentService contentService;
    @Inject
    ContentRepository contentRepository;
    @Inject
    UserService userService;
    @Inject
    UserRepository userRepository;
    @Inject
    private SimpMessagingTemplate simpMessagingTemplate;

    public void create(Long principalId, Long contentId) throws CustomException {

        Content content = contentService.getByRepoMethod(contentRepository.findById(contentId));

        User user = null;
        if (principalId != null) {
            user = userService.getByRepoMethod(userRepository.findById(principalId));
        }

        notificationService.createUpdateNotification(content.getUser(), user, NotificationType.SHARED, content, null, null);
        content.setUpdateTime(ZonedDateTime.now());
        content.setRShares(content.getRShares() + 1);

        contentRepository.save(content);

        if (content.getType().equals(ContentType.POST)) {
            UpdateContentNotificationDto updateContentNotificationDto = UpdateContentNotificationDto.builder().contentId(contentId).build();
            simpMessagingTemplate.convertAndSend("/content/update", updateContentNotificationDto);
        }
    }
}
