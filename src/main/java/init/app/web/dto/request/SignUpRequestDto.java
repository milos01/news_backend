package init.app.web.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by bojan.stankovic@codetri.be on 5/11/18.
 */
@Data
public class SignUpRequestDto {

    @NotNull
    @Size(min = 5, max = 254)
    @Pattern(regexp = "^([\\w-!#$%&'*+=?^_`{|}~]+(?:\\.[\\w-]+)*)@((?:[\\w-]+\\.)*\\w[\\w-]{0,66})\\.([A-Za-z]{2,6}(?:\\.[A-Za-z]{2})?)$")
    private String email;

    @NotNull
    @Size(min = 8, max = 120)
    @Pattern(regexp = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s)[0-9a-zA-Z!@#$%^&*()]*$")
    private String password;

}
