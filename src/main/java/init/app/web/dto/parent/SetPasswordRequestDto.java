package init.app.web.dto.parent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SetPasswordRequestDto implements Serializable{
    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(min = 1, max = 120)
    private String oldPassword;


    @NotNull
    @Size(min = 8, max = 120)
    @Pattern(regexp = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s)[0-9a-zA-Z!@#$%^&*()]*$")
    private String newPassword;
}
