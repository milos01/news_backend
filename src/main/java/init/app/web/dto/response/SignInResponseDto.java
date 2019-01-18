package init.app.web.dto.response;

import init.app.domain.model.enumeration.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SignInResponseDto implements Serializable{
    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(min = 64, max = 64)
    private String accessToken;

    @NotNull
    private String username;

    @NotNull
    private Role role;

    @NotNull
    private Long userId;

    @NotNull
    private String email;

    private String imageUrl;

    private List<SimpleTagResponseDto> tags;
}
