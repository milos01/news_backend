package init.app.web.dto.request;

import init.app.web.dto.parent.TagDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
//@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TagRequestDto extends TagDto{
    @Size(min = 2, max = 30)
    public String text;
}
