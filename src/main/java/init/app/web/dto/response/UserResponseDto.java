package init.app.web.dto.response;

import init.app.domain.model.enumeration.Role;
import init.app.web.dto.parent.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserResponseDto implements Serializable{

    private static final long serialVersionUID = 1L;

    private Long id;
    private Role role;
    private String email;
    private String username;
    private String bio;
    private String imageUrl;
    private Integer rFollowers;
    private Integer rFollowing;
}
