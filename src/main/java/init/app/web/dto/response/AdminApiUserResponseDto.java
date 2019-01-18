package init.app.web.dto.response;

import init.app.domain.model.enumeration.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Created by bojan.stankovic@codetri.be on 4/10/18.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminApiUserResponseDto {

    private Long id;
    private String username;
    private String email;
    private String imageUrl;
    private String bio;
    private String tags;
    private String twitterId;
    private String googleId;
    private String facebookId;
    private Role role;
    private ZonedDateTime createTime;
    private ZonedDateTime emailVerifiedTimestamp;
    private Boolean isDeleted;

}
