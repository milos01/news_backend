package init.app.domain.model;

import init.app.domain.model.enumeration.ChoiceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Entity
@Table(name = "vote")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "choice")
    private ChoiceType choice;

    @Column(name = "special_poll_nomination")
    private String specialPollNomination;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "poll_id")
    private Poll poll;

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
