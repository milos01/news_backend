package init.app.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Created by bojan.stankovic@codetri.be on 5/4/18.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PollOfTheDayResponseDto {

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
    private String rUserImageUrl;
    private String rMediaUrl;
    private String rTags;
    private String rPollQuestion;
    private String rPollAnswers;
    private Long userId;
    private Long repostId;
    private Long pollId;
    private Boolean liked;
    private Boolean voted;
    private Integer rUserFollowers;
    private Integer rUserFollowing;
    private Boolean following;
    private Boolean stored;
    private Long categoryId;

}
