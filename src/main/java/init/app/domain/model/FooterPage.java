package init.app.domain.model;

import init.app.domain.model.enumeration.FooterPageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Entity
@Table(name = "footer_page")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FooterPage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private FooterPageType type;

    @NotNull
    @Column(name = "order_number")
    private Integer order;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "content_id")
    private Content content;

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
