package init.app.web.dto.response;

import init.app.domain.model.enumeration.Role;
import init.app.web.dto.parent.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserPreviewResponseDto extends UserDto {

    private String email;
    private String username;
    private Role role;
    private String bio;
    private String imageUrl;
    private Integer rFollowers;
    private Integer rFollowing;
    private Boolean following;

}
