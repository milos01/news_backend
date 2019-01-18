package init.app.web.dto.mailchimp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MailChimpMergeFields {

    @JsonProperty("FNAME")
    private String firstName;

    @JsonProperty("LNAME")
    private String lastName;
}
