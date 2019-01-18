package init.app.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by bojan.stankovic@codetri.be on 4/11/18.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecialPollVoteResponseDto {

    private Long userId;
    private String username;
    private String nomination;

}
