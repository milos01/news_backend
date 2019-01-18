package init.app.domain.model;

import init.app.domain.model.enumeration.PollType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Entity
@Table(name = "poll")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private PollType type;

    @Size(max = 512)
    @Column(name = "question")
    private String question;

    @Size(max = 512)
    @Column(name = "first_choice")
    private String firstChoice;

    @Size(max = 512)
    @Column(name = "second_choice")
    private String secondChoice;

    @Size(max = 512)
    @Column(name = "third_choice")
    private String thirdChoice;

    @NotNull
    @Column(name = "create_time")
    private ZonedDateTime createTime;

    @NotNull
    @Column(name = "update_time")
    private ZonedDateTime updateTime;

    @NotNull
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "r_first_amount")
    private Integer rFirstAmount;

    @Column(name = "r_second_amount")
    private Integer rSecondAmount;

    @Column(name = "r_third_amount")
    private Integer rThirdAmount;

}
