package init.app.domain.model;

import init.app.domain.model.enumeration.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Entity
@Table(name = "comment")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(max = 2000)
    @Column(name = "text")
    private String text;

    @Size(max = 256)
    @Column(name = "url")
    private String url;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "content_id")
    private Content content;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @NotNull
    @Column(name = "create_time")
    private ZonedDateTime createTime;

    @NotNull
    @Column(name = "update_time")
    private ZonedDateTime updateTime;

    @NotNull
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Size(max = 256)
    @Column(name = "r_username")
    private String rUsername;

    @Enumerated(EnumType.STRING)
    @Column(name = "r_user_role")
    private Role rUserRole;

    @Size(max = 256)
    @Column(name = "r_user_image_url")
    private String rUserImageUrl;

    @Column(name = "r_replies")
    private Integer rReplies;

}
