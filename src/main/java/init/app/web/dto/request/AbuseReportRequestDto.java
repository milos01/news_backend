package init.app.web.dto.request;

import init.app.domain.model.enumeration.AbuseReportReason;
import init.app.domain.model.enumeration.AbuseReportType;
import init.app.web.dto.parent.AbuseReportDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AbuseReportRequestDto {

    private Long commentId;
    private Long contentId;
    private AbuseReportType type;
    private AbuseReportReason abuseReportReason;
    private String feedback;
}
