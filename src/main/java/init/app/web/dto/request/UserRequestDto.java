package init.app.web.dto.request;

import init.app.web.dto.parent.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserRequestDto extends UserDto {
    @Size(min = 3, max = 30)
    private String username;
    @Size(max = 300)
    private String bio;
}
