package init.app.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Created by bojan.stankovic@codetri.be on 2/14/18.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentLikesResponseDto {

    private Long likeId;
    private Long userId;
    private String userEmail;
    private String username;
    private String userBio;
    private String userImageUrl;

}
