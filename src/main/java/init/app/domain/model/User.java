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
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Size(max = 256)
    @Column(name = "email")
    private String email;

    @Size(max = 128)
    @Column(name = "password")
    private String password;

    @Size(max = 512)
    @Column(name = "username")
    private String username;

    @Size(max = 512)
    @Column(name = "bio")
    private String bio;

    @Size(max = 256)
    @Column(name = "image_url")
    private String imageUrl;

    @Size(max = 128)
    @Column(name = "facebook_id")
    private String facebookId;

    @Size(max = 128)
    @Column(name = "twitter_id")
    private String twitterId;

    @Size(max = 128)
    @Column(name = "google_id")
    private String googleId;

    @NotNull
    @Column(name = "createTime")
    private ZonedDateTime createTime;

    @NotNull
    @Column(name = "updateTime")
    private ZonedDateTime updateTime;

    @Column(name = "email_verified_time")
    private ZonedDateTime emailVerifiedTime;

    @NotNull
    @Column(name = "isDeleted")
    private Boolean isDeleted;

    @Column(name = "r_tags")
    private String rTags;

    @Column(name = "r_followers")
    private Integer rFollowers;

    @Column(name = "r_following")
    private Integer rFollowing;
}
