package init.app.domain.repository;

import init.app.domain.model.*;
import init.app.domain.model.enumeration.NotificationType;
import init.app.web.dto.parent.NotificationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query(value = "SELECT noti.id, noti.type, noti.r_users, noti.r_content_headline, noti.content_id, noti.comment_id, noti.poll_id, noti.is_read, noti.update_time, cont.type as content_type " +
            "FROM notification noti " +
            "LEFT JOIN content cont on cont.id = noti.content_id " +
            "WHERE noti.user_id = :userId AND noti.is_deleted = FALSE ORDER BY noti.is_read ASC, noti.update_time DESC LIMIT :limit OFFSET :offset",
            countQuery = "SELECT n.id FROM notification n WHERE n.user_id = :userId AND n.is_deleted = FALSE",
            nativeQuery = true)
    List<Object[]> getAll(@Param("userId")Long userId, @Param("limit") Integer limit, @Param("offset") Integer offset);

    @Query(value = "SELECT new init.app.web.dto.parent.NotificationResponseDto(noti.id, noti.type, noti.rUsers, noti.rContentHeadline, noti.content.id, noti.comment.id, noti.poll.id, noti.isRead, noti.updateTime, noti.content.type) " +
            "FROM Notification noti " +
            "WHERE noti.id = :id")
    NotificationResponseDto getNotification(@Param("id") Long id);

    @Query(value = "SELECT id FROM notification",
            nativeQuery = true)
    List<Object[]> getAllSharesForContent(Long contentId);

    @Query(value = "SELECT id FROM notification ORDER BY ?#{#pageable}",
            countQuery = "SELECT * FROM notification ORDER BY ?#{#pageable}",
            nativeQuery = true)
    Page<Object> getUserShares(Long userId, Pageable pageable);

    Notification getByCommentAndContentAndPollAndUserAndTypeAndIsDeletedFalse(Comment comment, Content content, Poll poll, User user, NotificationType type);

    Integer countAllByUserAndIsReadFalseAndIsDeletedFalse(User user);

    List<Notification> findAllByRUsersContainingAndIsDeletedFalse(String users);

    List<Notification> findAllByContent(Content content);

}
