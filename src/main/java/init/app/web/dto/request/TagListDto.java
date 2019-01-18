package init.app.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by bojan.stankovic@codetri.be on 2/13/18.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagListDto {

    List<AssignTagRequestDto> tagList;

}
