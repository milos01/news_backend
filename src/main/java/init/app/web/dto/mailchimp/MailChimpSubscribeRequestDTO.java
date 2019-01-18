package init.app.web.dto.mailchimp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MailChimpSubscribeRequestDTO {

    @JsonProperty("email_address")
    private String emailAddress;

    @JsonProperty("status")
    private String status;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("merge_fields")
    private MailChimpMergeFields mergeFields;
}
