package init.app.domain.repository;

import init.app.domain.model.AbuseReport;
import init.app.domain.model.Comment;
import init.app.domain.model.Content;
import init.app.domain.model.enumeration.AbuseReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AbuseReportRepository extends JpaRepository<AbuseReport, Long> {

    @Query(value = "SELECT abuse_report.id AS abuse_report_id, abuse_report.type, abuse_report.reason, abuse_report.abuse_report_status, abuse_report.create_time, abuse_report.feedback, comment.id AS comment_id, comment.text, IF(comment.id IS NOT NULL, IF(comment.parent_comment_id IS NOT NULL, 'REPLY', 'COMMENT'), null), content.id AS content_id, content.repost_id, content.headline, content.type AS content_type, author.id AS author_id, author.username AS author_username, reported_user.id, reported_user.username " +
            "FROM abuse_report " +
            "INNER JOIN content ON content.id = abuse_report.content_id " +
            "LEFT JOIN comment ON comment.id = abuse_report.comment_id " +
            "INNER JOIN user AS author ON author.id = abuse_report.user_id " +
            "LEFT JOIN user AS reported_user ON IF(abuse_report.type = 'COMMENT', reported_user.id = comment.user_id, reported_user.id = content.user_id) " +
            "WHERE IF(:type IS NOT NULL, abuse_report.type = :type, abuse_report.id = abuse_report.id) AND IF(:abuseReportStatus IS NOT NULL, abuse_report.abuse_report_status = :abuseReportStatus, abuse_report.id = abuse_report.id) AND IF(:reason IS NOT NULL, abuse_report.reason = :reason, abuse_report.id = abuse_report.id) AND abuse_report.is_deleted = FALSE " +
            "ORDER BY abuse_report.create_time DESC, ?#{#pageable}",
            countQuery = "SELECT abuse_report.id " +
            "FROM abuse_report " +
            "INNER JOIN content ON content.id = abuse_report.content_id " +
            "LEFT JOIN comment on comment.id = abuse_report.comment_id " +
            "INNER JOIN user AS author ON author.id = abuse_report.user_id " +
            "LEFT JOIN user AS reported_user ON IF(abuse_report.type = 'COMMENT', reported_user.id = comment.user_id, reported_user.id = content.user_id) " +
            "WHERE IF(:type IS NOT NULL, abuse_report.type = :type, abuse_report.id = abuse_report.id) AND IF(:abuseReportStatus IS NOT NULL, abuse_report_status = :abuseReportStatus, abuse_report.id = abuse_report.id) AND IF(:reason IS NOT NULL, abuse_report.reason = :reason, abuse_report.id = abuse_report.id) AND abuse_report.is_deleted = FALSE " +
            "ORDER BY ?#{#pageable}",
    nativeQuery = true)
    Page<Object[]> getAll(@Param("type") String type, @Param("abuseReportStatus") String abuseReportStatus, @Param("reason") String reason, Pageable pageable);

    AbuseReport findById(Long abuseReportId);

    List<AbuseReport> findByCommentAndIsDeletedFalseAndAbuseReportStatus(Comment comment, AbuseReportStatus abuseReportStatus);

    List<AbuseReport> findByContentAndIsDeletedFalseAndAbuseReportStatus(Content content, AbuseReportStatus abuseReportStatus);
}
