package init.app.domain.model;

import init.app.domain.model.enumeration.ContentType;
import init.app.domain.model.enumeration.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Entity
@Table(name = "content")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ContentType type;

    @Size(max = 256)
    @Column(name = "headline")
    private String headline;

    @Column(name = "text")
    private String text;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "poll_id")
    private Poll poll;

    @ManyToOne
    @JoinColumn(name = "repost_id")
    private Content content;

    @NotNull
    @Column(name = "total_activity")
    private Integer totalActivity;

    @NotNull
    @Column(name = "create_time")
    private ZonedDateTime createTime;

    @NotNull
    @Column(name = "update_time")
    private ZonedDateTime updateTime;

    @NotNull
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "r_likes")
    private Integer rLikes;

    @Column(name = "r_reposts")
    private Integer rReposts;

    @Column(name = "r_shares")
    private Integer rShares;

    @Column(name = "r_comments")
    private Integer rComments;

    @Column(name = "r_has_video")
    private Boolean rHasVideo;

    @Size(max = 256)
    @Column(name = "r_username")
    private String rUsername;

    @Enumerated(EnumType.STRING)
    @Column(name = "r_user_role")
    private Role rUserRole;

    @Size(max = 256)
    @Column(name = "r_user_image_url")
    private String rUserImageUrl;

    @Size(max = 10240)
    @Column(name = "r_media_url")
    private String rMediaUrl;

    @Size(max = 1024)
    @Column(name = "r_tags")
    private String rTags;

    @Column(name = "r_reactions")
    private String rReactions;

    @Size(max = 512)
    @Column(name = "r_poll_question")
    private String rPollQuestion;

    @Column(name = "r_poll_answers")
    private String rPollAnswers;

}
