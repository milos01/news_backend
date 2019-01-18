package init.app.domain.repository;

import init.app.domain.model.Comment;
import init.app.domain.model.User;
import init.app.web.dto.response.CommentResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Comment findById(Long id);

    @Query(value = "SELECT c.id, c.text, c.url, c.user_id, c.r_replies, c.r_user_image_url, c.r_username, c.r_user_role, c.create_time, c.update_time FROM comment c WHERE (c.parent_comment_id = :parentCommentId or (:parentCommentId is null and c.parent_comment_id is null)) AND c.content_id = :contentId and c.is_deleted = false ORDER BY c.create_time ASC LIMIT :limitParam OFFSET :offsetParam", nativeQuery = true)
    List<Object[]> getAll(@Param("contentId") Long contentId, @Param("parentCommentId") Long parentCommentId, @Param("limitParam") Integer limit, @Param("offsetParam") Integer offset);

    @Query(value = "SELECT new init.app.web.dto.response.CommentResponseDto(c.id, c.text, c.url, c.user.id, c.rReplies, c.rUserImageUrl, c.rUsername, c.rUserRole, c.createTime, c.updateTime) FROM Comment c WHERE c.id = :commentId and c.isDeleted = false")
    CommentResponseDto getComment(@Param("commentId") Long commentId);

    List<Comment> findAllByParentCommentAndIsDeletedFalse(Comment comment);

    List<Comment> findAllByUserAndIsDeletedFalse(User user);

    // TODO: 6/7/18 DELETE AFTER MIGRATIONS
    Comment findFirstByCreateTime(ZonedDateTime createTime);
}