package init.app.web.dto.response;

import init.app.domain.model.enumeration.Role;
import init.app.web.dto.parent.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserVerifyLinkResponseDto extends UserDto {

    private Role role;
    private String email;
    private String username;
    private String bio;
    private String imageUrl;

    @Size(min = 64, max = 64)
    private String accessToken;

    public UserVerifyLinkResponseDto(Long id, Role role, String email, String username, String bio, String imageUrl) {
        super(id);
        this.role = role;
        this.email = email;
        this.username = username;
        this.bio = bio;
        this.imageUrl = imageUrl;
    }
}
