package init.app.domain.model;

import init.app.domain.model.enumeration.AbuseReportReason;
import init.app.domain.model.enumeration.AbuseReportStatus;
import init.app.domain.model.enumeration.AbuseReportType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Entity
@Table(name = "abuse_report")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AbuseReport {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private AbuseReportType type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "content_id")
    private Content content;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "reason")
    private AbuseReportReason reason;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "abuse_report_status")
    private AbuseReportStatus abuseReportStatus;

    @Size(max = 512)
    @Column(name = "feedback")
    private String feedback;

    @NotNull
    @Column(name = "create_time")
    private ZonedDateTime createTime;

    @NotNull
    @Column(name = "update_time")
    private ZonedDateTime updateTime;

    @NotNull
    @Column(name = "is_deleted")
    private Boolean isDeleted;

}
