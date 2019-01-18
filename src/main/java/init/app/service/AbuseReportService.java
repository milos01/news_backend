package init.app.service;

import com.jcabi.aspects.Loggable;
import init.app.domain.model.AbuseReport;
import init.app.domain.model.Comment;
import init.app.domain.model.Content;
import init.app.domain.model.User;
import init.app.domain.model.enumeration.AbuseReportReason;
import init.app.domain.model.enumeration.AbuseReportStatus;
import init.app.domain.model.enumeration.AbuseReportType;
import init.app.domain.repository.AbuseReportRepository;
import init.app.domain.repository.CommentRepository;
import init.app.domain.repository.ContentRepository;
import init.app.domain.repository.UserRepository;
import init.app.exception.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

@Service
@Loggable(trim = false, prepend = true)
@Transactional(rollbackFor = Exception.class)
public class AbuseReportService {

    @Inject
    AbuseReportRepository abuseReportRepository;
    @Inject
    ContentService contentService;
    @Inject
    ContentRepository contentRepository;
    @Inject
    CommentService commentService;
    @Inject
    CommentRepository commentRepository;
    @Inject
    UserService userService;
    @Inject
    UserRepository userRepository;

    public void create(Long commentId, Long contentId, Long reportAuthorId, AbuseReportType type, AbuseReportReason abuseReportReason, String feedback) throws CustomException {

        Comment comment = null;
        Content content = null;
        User reportAuthor = userService.getByRepoMethod(userRepository.findById(reportAuthorId));

        switch (type) {
            case CONTENT:
                content = contentService.getByRepoMethod(contentRepository.findById(contentId));
                break;
            case COMMENT:
                comment = commentService.getByRepoMethod(commentRepository.findById(commentId));
                content = contentService.getByRepoMethod(contentRepository.findById(comment.getContent().getId()));
                break;
        }

        createAbuseReport(comment, content, reportAuthor, type, abuseReportReason, feedback);
    }

    public Long createAbuseReport(Comment comment, Content content, User reportAuthor, AbuseReportType type, AbuseReportReason abuseReportReason, String feedback) {

        AbuseReport abuseReport = new AbuseReport();
        abuseReport.setComment(comment);
        abuseReport.setContent(content);
        abuseReport.setUser(reportAuthor);
        abuseReport.setType(type);
        abuseReport.setReason(abuseReportReason);
        abuseReport.setFeedback(feedback);
        abuseReport.setCreateTime(ZonedDateTime.now());
        abuseReport.setUpdateTime(ZonedDateTime.now());
        abuseReport.setAbuseReportStatus(AbuseReportStatus.PENDING);
        abuseReport.setIsDeleted(false);

        abuseReportRepository.save(abuseReport);

        return abuseReport.getId();
    }

    public void confirm(Long abuseReportId) throws CustomException {

        AbuseReport abuseReport = getByRepoMethod(abuseReportRepository.findById(abuseReportId));

        for (AbuseReport reportToConfirm : abuseReport.getType() == AbuseReportType.COMMENT ? abuseReportRepository.findByCommentAndIsDeletedFalseAndAbuseReportStatus(abuseReport.getComment(), AbuseReportStatus.PENDING) : abuseReportRepository.findByContentAndIsDeletedFalseAndAbuseReportStatus(abuseReport.getContent(), AbuseReportStatus.PENDING)) {
            confirmAbuseReport(reportToConfirm);
        }
    }

    public void confirmAbuseReport(AbuseReport abuseReport) throws CustomException {

        if (abuseReport.getAbuseReportStatus() != AbuseReportStatus.PENDING) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("ABUSE_REPORT_FINALIZED"));
        }

        abuseReport.setAbuseReportStatus(AbuseReportStatus.COMPLETED);
        abuseReport.setUpdateTime(ZonedDateTime.now());
        abuseReportRepository.save(abuseReport);
    }

    public void deny(Long abuseReportId) throws CustomException {

        AbuseReport abuseReport = getByRepoMethod(abuseReportRepository.findById(abuseReportId));

        denyAbuseReport(abuseReport);
    }

    public void denyAbuseReport(AbuseReport abuseReport) throws CustomException {

        if (abuseReport.getAbuseReportStatus() != AbuseReportStatus.PENDING) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("ABUSE_REPORT_FINALIZED"));
        }

        abuseReport.setAbuseReportStatus(AbuseReportStatus.DENIED);
        abuseReport.setUpdateTime(ZonedDateTime.now());
        abuseReportRepository.save(abuseReport);

    }

    public AbuseReport getByRepoMethod(AbuseReport abuseReport) throws CustomException {

        if (abuseReport == null) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("ABUSE_REPORT_NOT_EXIST"));
        } else if (abuseReport.getIsDeleted()) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("ABUSE_REPORT_DELETED"));
        }

        return abuseReport;
    }

}
