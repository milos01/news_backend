package init.app.web.dto.mailchimp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EmailMergeFieldsRequestDTO {

    @NotNull
    private String email;

    private String firstName;

    private String lastName;
}
