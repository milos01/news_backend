package init.app.domain.model;

import init.app.domain.model.enumeration.AbuseReportType;
import init.app.domain.model.enumeration.AdType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Entity
@Table(name = "ad")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private AdType type;

    @NotNull
    @Size(max = 512)
    @Column(name = "href")
    private String href;

    @NotNull
    @Size(max = 512)
    @Column(name = "image_url")
    private String imageUrl;

    @NotNull
    @Column(name = "total_activity")
    private Integer totalActivity;

    @NotNull
    @Column(name = "temp_activity")
    private Integer tempActivity;

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
