package init.app.domain.model;

import init.app.domain.model.enumeration.TagType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Entity
@Table(name = "tag")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TagType type;

    @NotNull
    @Size(max = 512)
    @Column(name = "text")
    private String text;

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

    @Column(name = "r_followers")
    private Integer rFollowers;

}
