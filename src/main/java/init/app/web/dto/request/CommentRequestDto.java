package init.app.web.dto.request;

import init.app.web.dto.parent.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
//@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CommentRequestDto extends CommentDto {
    @Size(min = 2, max = 2000)
    private String text;
}
