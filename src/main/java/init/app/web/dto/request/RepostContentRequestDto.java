package init.app.web.dto.request;

import init.app.web.dto.parent.ContentDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by bojan.stankovic@codetri.be on 2/21/18.
 */

@Data
@AllArgsConstructor
//@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RepostContentRequestDto extends ContentDto {

    private String text;
}
