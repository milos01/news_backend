package init.app.service;

import com.jcabi.aspects.Loggable;
import init.app.domain.model.Comment;
import init.app.domain.model.Content;
import init.app.domain.model.User;
import init.app.domain.model.enumeration.ContentType;
import init.app.domain.model.enumeration.NotificationType;
import init.app.domain.model.enumeration.Role;
import init.app.domain.repository.CommentRepository;
import init.app.domain.repository.ContentRepository;
import init.app.domain.repository.UserRepository;
import init.app.exception.CustomException;
import init.app.web.dto.custom.UpdateContentNotificationDto;
import init.app.web.dto.parent.IdDto;
import init.app.web.dto.shared.GenericResponseDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.servlet.ServletException;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

@Service
@Loggable(trim = false, prepend = true)
@Transactional(rollbackFor = Exception.class)
public class CommentService {

    @Inject
    CommentRepository commentRepository;
    @Inject
    ContentRepository contentRepository;
    @Inject
    ContentService contentService;
    @Inject
    UserService userService;
    @Inject
    UserRepository userRepository;
    @Inject
    FileService fileService;
    @Inject
    private NotificationService notificationService;
    @Inject
    private MentionService mentionService;
    @Inject
    private SimpMessagingTemplate simpMessagingTemplate;

    public GenericResponseDto create(String text, Long userId, Long contentId, Optional<Long> parentCommentId) throws CustomException {

        Optional<User> user = Optional.ofNullable(userId != null ? userService.getByRepoMethod(userRepository.findById(userId)) : null);

        Long commentId = createComment(text, contentId, user, parentCommentId, Optional.empty());

        return new GenericResponseDto(new IdDto(commentId));
    }

    public Long createComment(String text, Long contentId, Optional<User> user, Optional<Long> parentCommentId, Optional<String> anonymousUserName) throws CustomException {

        Comment comment = new Comment();

        Comment parentComment = null;

        if (parentCommentId.isPresent()) {
            parentComment = getByRepoMethod(commentRepository.findById(parentCommentId.get()));
            parentComment.setRReplies(parentComment.getRReplies() + 1);

            commentRepository.save(parentComment);

            comment.setParentComment(parentComment);
        }

        Content content = contentService.getByRepoMethod(contentRepository.findById(contentId));

        comment.setContent(content);

        if (user.isPresent()) {
            comment.setUser(user.get());
            comment.setRUserImageUrl(user.get().getImageUrl());
            comment.setRUsername(user.get().getUsername());
            comment.setRUserRole(user.get().getRole());
        } else {
            comment.setUser(null);
            comment.setRUserImageUrl(null);
            comment.setRUsername("Anonymous user");
        }

        comment.setCreateTime(ZonedDateTime.now());
        comment.setUpdateTime(ZonedDateTime.now());
        comment.setIsDeleted(false);
        comment.setText(text);
        comment.setRReplies(0);

        commentRepository.save(comment);

        if (parentComment != null) {
            if (parentComment.getUser() != null) {
                notificationService.createUpdateNotification(parentComment.getUser(), user.orElse(null), NotificationType.REPLIED_TO_COMMENT, content, parentComment, null);
            }
        } else {
            notificationService.createUpdateNotification(content.getUser(), user.orElse(null), NotificationType.COMMENTED, content, null, null);
        }

        mentionService.createMentionsForComment(comment);

        if (content.getType().equals(ContentType.POST)) {
            UpdateContentNotificationDto updateContentNotificationDto = UpdateContentNotificationDto.builder().contentId(content.getId()).build();
            simpMessagingTemplate.convertAndSend("/content/update", updateContentNotificationDto);
        }

        return comment.getId();
    }

    public void delete(Long principalId, Long commentId) throws CustomException {
        Comment comment = getByRepoMethod(commentRepository.findById(commentId));

        User user = userService.getByRepoMethod(userRepository.findById(principalId));

        if (user.getRole() != Role.ADMIN && !comment.getUser().getId().equals(principalId)) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("COMMENT_NOT_MINE"));
        }

        deleteComment(comment);

        if (comment.getContent().getType().equals(ContentType.POST)) {
            UpdateContentNotificationDto updateContentNotificationDto = UpdateContentNotificationDto.builder().contentId(comment.getContent().getId()).build();
            simpMessagingTemplate.convertAndSend("/content/update", updateContentNotificationDto);
        }
    }

    public void deleteComment(Comment comment) {
        if(comment.getParentComment() != null){
            Comment parentComment = comment.getParentComment();
            parentComment.setRReplies(parentComment.getRReplies() - 1);

            if(!parentComment.getIsDeleted()){
                commentRepository.save(parentComment);
            }
        }

        comment.setIsDeleted(true);
        comment.setUpdateTime(ZonedDateTime.now());
        commentRepository.save(comment);

        List<Comment> childComments = commentRepository.findAllByParentCommentAndIsDeletedFalse(comment);

        for (Comment childComment : childComments) {
            deleteComment(childComment);
        }
    }

    public void uploadImage(Long principalId, Long commentId, MultipartFile file) throws CustomException {

        Comment comment = getByRepoMethod(commentRepository.findById(commentId));

        if (principalId != null) {
            User user = userService.getByRepoMethod(userRepository.findById(principalId));

            if (user.getRole() != Role.ADMIN && !comment.getUser().getId().equals(principalId)) {
                throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("COMMENT_NOT_MINE"));
            }

            String key;
            try {
                key = fileService.getMediaUrl(file);
            } catch (IOException | ServletException s) {
                throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("BUCKET_UPLOAD"));
            }

            updateImageUrl(commentId, key);
        }// TODO: 3/7/18 uraditi za anonymus usere
    }

    @Async
    public void updateImageUrl(Long commentId, String imageUrl) throws CustomException {

        Comment comment = getByRepoMethod(commentRepository.findById(commentId));
        comment.setUrl(imageUrl);
        comment.setUpdateTime(ZonedDateTime.now());
        commentRepository.save(comment);
    }

    public Comment getByRepoMethod(Comment comment) throws CustomException {

        if (comment == null) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("COMMENT_NOT_EXIST"));
        } else if (comment.getIsDeleted()) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("COMMENT_DELETED"));
        }

        return comment;
    }

}
