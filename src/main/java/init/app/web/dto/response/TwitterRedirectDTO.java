package init.app.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by bojan.stankovic@codetri.be on 3/8/18.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TwitterRedirectDTO {
    private String redirectUrl;
}
