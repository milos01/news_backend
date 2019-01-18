package init.app.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Created by bojan.stankovic@codetri.be on 5/3/18.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingleContentResponseDto {

    private Long id;
    private String headline;
    private String text;
    private String rMediaUrl;
    private ZonedDateTime createTime;
    private Long categoryId;
    private Long pollId;
    private Boolean isFollowingPoll;
    private String rPollQuestion;
    private String rPollAnswers;
    private String rTags;

}
