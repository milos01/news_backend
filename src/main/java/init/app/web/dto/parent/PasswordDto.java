package init.app.web.dto.parent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PasswordDto implements Serializable{
    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(min = 4, max = 32)
    private String password;
}
