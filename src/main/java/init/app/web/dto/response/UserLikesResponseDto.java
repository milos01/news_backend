package init.app.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by bojan.stankovic@codetri.be on 2/14/18.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLikesResponseDto {

    private Long likeId;
    private Long contentId;
    private String contentHeadline;
    private String contentMediaUrl;

}