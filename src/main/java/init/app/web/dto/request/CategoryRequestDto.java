package init.app.web.dto.request;

import init.app.web.dto.parent.CategoryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
//@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CategoryRequestDto extends CategoryDto {

}
