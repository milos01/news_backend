package init.app.web.dto.request;

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
public class AuthSocialRequestDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(min = 6, max = 256)
    @Pattern(regexp = "^([\\w-!#$%&'*+=?^_`{|}~]+(?:\\.[\\w-]+)*)@((?:[\\w-]+\\.)*\\w[\\w-]{0,66})\\.([A-Za-z]{2,6}(?:\\.[A-Za-z]{2})?)$")
    private String email;

    private String userName;

    @NotNull
    private String socialId;

    private String imageUrl;
}
