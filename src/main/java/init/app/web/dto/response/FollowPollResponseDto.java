package init.app.web.dto.response;

import init.app.domain.model.enumeration.ContentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowPollResponseDto {
    private Long contentId;
    private ContentType contentType;
    private String pollQuestion;
    private String pollAnswers;
}
