package init.app.web.dto.response;

import init.app.web.dto.parent.ContentDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContentResponseDto extends ContentDto {

    private Long id;
    private String headline;
    private String text;
    private ZonedDateTime createTime;
    private Integer rLikes;
    private Integer rReposts;
    private Integer rShares;
    private Integer rComments;
    private Boolean rHasVideo;
    private String rUsername;
    private String rUserRole;
    private String rUserImageUrl;
    private String rMediaUrl;
    private String rTags;
    private String rPollQuestion;
    private String rPollAnswers;
    private String rReactions;
    private Long userId;
    private Long repostId;
    private Long pollId;
    private Boolean isFollowingPoll;
    private Boolean liked;
    private Boolean voted;
    private Integer rUserFollowers;
    private Integer rUserFollowing;
    private Boolean following;
    private Boolean stored;
    private Boolean commented;
    private Boolean reposted;
}
