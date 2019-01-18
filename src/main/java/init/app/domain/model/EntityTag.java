package init.app.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Entity
@Table(name = "entity_tag")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EntityTag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "content_id")
    private Content content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

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
