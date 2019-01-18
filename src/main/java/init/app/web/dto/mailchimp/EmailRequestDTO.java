package init.app.web.dto.mailchimp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EmailRequestDTO {

    @NotNull
    @Size(min = 5, max = 254)
    @Pattern(regexp = "^([\\w-!#$%&'*+=?^_`{|}~]+(?:\\.[\\w-]+)*)@((?:[\\w-]+\\.)*\\w[\\w-]{0,66})\\.([A-Za-z]{2,6}(?:\\.[A-Za-z]{2})?)$")
    private String email;
}
