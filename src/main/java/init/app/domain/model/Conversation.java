package init.app.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Entity
@Table(name = "conversation")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "create_time")
    private ZonedDateTime createTime;

    @NotNull
    @Column(name = "update_time")
    private ZonedDateTime updateTime;

    @NotNull
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "r_users")
    private String rUsers;

    @Column(name = "r_users_info")
    private String rUsersInfo;

    @Column(name = "last_message_time")
    private ZonedDateTime lastMessageTime;

}
