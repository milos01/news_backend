package init.app.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Size;

/**
 * Created by bojan.stankovic@codetri.be on 6/7/18.
 */

@Data
@AllArgsConstructor
public class AssignTagRequestDto {

    private Long id;

    @Size(min = 3, max = 30)
    private String text;

}
