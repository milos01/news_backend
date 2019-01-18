package init.app.web.dto.response;


import init.app.domain.model.enumeration.PollType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by bojan.stankovic@codetri.be on 4/5/18.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentPollResponseDto {

    private Long authorId;
    private String authorUsername;
    private String authorEmail;
    private Long contentId;
    private String contentTitle;
    private String contentText;
    private Long pollId;
    private PollType pollType;

}
