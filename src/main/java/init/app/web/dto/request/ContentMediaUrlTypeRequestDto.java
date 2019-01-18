package init.app.web.dto.request;

import init.app.domain.model.enumeration.ContentMediaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by bojan.stankovic@codetri.be on 3/13/18.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentMediaUrlTypeRequestDto extends UrlTypeRequestDto {

    private Integer order;
}
