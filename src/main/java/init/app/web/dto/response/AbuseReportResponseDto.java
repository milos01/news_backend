package init.app.web.dto.response;

import init.app.domain.model.enumeration.AbuseReportReason;
import init.app.domain.model.enumeration.AbuseReportStatus;
import init.app.domain.model.enumeration.AbuseReportType;
import init.app.domain.model.enumeration.ContentType;
import init.app.web.dto.parent.AbuseReportDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AbuseReportResponseDto {

    private Long id;
    private AbuseReportType type;
    private AbuseReportReason reason;
    private AbuseReportStatus status;
    private ZonedDateTime timestamp;
    private String feedback;
    private Long commentId;
    private String commentText;
    private String commentType;
    private Long contentId;
    private Long repostId;
    private String contentHeadline;
    private ContentType contentType;
    private Long reportAuthorId;
    private String reportAuthorUsername;
    private Long reportedUserId;
    private String reportedUsername;

}
