package init.app.web.dto.response;

import init.app.web.dto.parent.TagDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by bojan.stankovic@codetri.be on 3/16/18.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleTagResponseDto extends TagDto {

    private Long id;
    private String text;

}
