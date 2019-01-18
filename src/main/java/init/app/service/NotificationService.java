package init.app.service;

import com.jcabi.aspects.Loggable;
import init.app.domain.model.*;
import init.app.domain.model.enumeration.ContentType;
import init.app.domain.model.enumeration.NotificationType;
import init.app.domain.model.enumeration.Role;
import init.app.domain.repository.NotificationRepository;
import init.app.domain.repository.UserRepository;
import init.app.exception.CustomException;
import init.app.web.dto.custom.NewNotificationDto;
import init.app.web.dto.custom.NotificationCounterDto;
import init.app.web.dto.parent.NotificationResponseDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Loggable(trim = false, prepend = true)
@Transactional(rollbackFor = Exception.class)
public class NotificationService {

    @Inject
    NotificationRepository notificationRepository;

    @Inject
    SimpMessagingTemplate simpMessagingTemplate;

    @Inject
    UserRepository userRepository;

    private static final String ANONYMOUS_USER = "Anonymous user";

    public void createUpdateNotification(User notificationForUser, User notificationByUser, NotificationType type, Content content, Comment comment, Poll poll) throws CustomException {

        if ((notificationForUser != null && notificationByUser != null && Objects.equals(notificationByUser.getId(), notificationForUser.getId())) || (notificationForUser == null && notificationByUser == null) || notificationForUser != null && notificationForUser.getRole().equals(Role.DEACTIVATED)) {
            return;
        }

        Notification existingNotification = notificationRepository.getByCommentAndContentAndPollAndUserAndTypeAndIsDeletedFalse(comment, content, poll, notificationForUser, type);

        if (existingNotification != null) {
            updateNotification(existingNotification, notificationByUser);
        } else {
            existingNotification = create(notificationForUser, notificationByUser, type, content, comment, poll);
        }

        NotificationCounterDto notificationCounterDto = new NotificationCounterDto();
        notificationCounterDto.setUnreadCounter(notificationRepository.countAllByUserAndIsReadFalseAndIsDeletedFalse(notificationForUser));

        simpMessagingTemplate.convertAndSend("/notification/user/" + notificationForUser.getId(), notificationCounterDto);

        NewNotificationDto newNotificationDto = new NewNotificationDto();
        newNotificationDto.setNotificationId(existingNotification.getId());

        simpMessagingTemplate.convertAndSend("/notification/user/" + notificationForUser.getId() + "/new", newNotificationDto);
    }

    public void updateNotification(Notification notification, User user) {

        notification.setUpdateTime(ZonedDateTime.now());
        notification.setIsRead(false);

        if (notification.getRUsers() == null || user != null && !notification.getRUsers().contains(user.getUsername() + "/" + user.getId()) || user == null && !notification.getRUsers().contains(ANONYMOUS_USER + "/0") && !notification.getRUsers().contains(ANONYMOUS_USER + "s/0")) {
            notification.setRUsers(notification.getRUsers() + "|" + (user != null ? user.getUsername() : ANONYMOUS_USER) + "/" + (user != null ? user.getId() : 0));
        } else if (user == null && notification.getRUsers().contains(ANONYMOUS_USER) && !notification.getRUsers().contains(ANONYMOUS_USER + "s")) {
            notification.setRUsers(notification.getRUsers().replace(ANONYMOUS_USER, ANONYMOUS_USER + "s"));
        }

        notificationRepository.save(notification);
    }

    public Notification create(User notificationForUser, User notificationByUser, NotificationType type, Content content, Comment comment, Poll poll) {

        Notification notification = new Notification();
        notification.setUser(notificationForUser);
        notification.setContent(content);
        notification.setComment(comment);
        notification.setPoll(poll);
        notification.setType(type);
        notification.setRUsers((notificationByUser != null ? notificationByUser.getUsername() : ANONYMOUS_USER) + "/" + (notificationByUser != null ? notificationByUser.getId() : 0));
        notification.setIsRead(false);
        notification.setIsDeleted(false);
        notification.setCreateTime(ZonedDateTime.now());
        notification.setUpdateTime(ZonedDateTime.now());
        notification.setRContentHeadline(content != null && content.getType() != ContentType.POST ? content.getHeadline() : "");

        notificationRepository.save(notification);

        return notification;
    }

    @Async
    public void setUnreadNotificationsToRead(List<NotificationResponseDto> allNotifications, Long userId) {

        Boolean notificationCounterChanged = false;

        for (NotificationResponseDto notification : allNotifications) {
            Notification notificationEntity = notificationRepository.getOne(notification.getId());

            if (notificationEntity.getIsRead()) {
                break;
            }

            notificationCounterChanged = true;

            notificationEntity.setIsRead(true);
            notificationRepository.save(notificationEntity);
        }

        if (notificationCounterChanged) {
            User user = userRepository.findById(userId);

            NotificationCounterDto notificationCounterDto = new NotificationCounterDto();
            notificationCounterDto.setUnreadCounter(notificationRepository.countAllByUserAndIsReadFalseAndIsDeletedFalse(user));

            simpMessagingTemplate.convertAndSend("/notification/user/" + user.getId(), notificationCounterDto);
        }

    }
}
