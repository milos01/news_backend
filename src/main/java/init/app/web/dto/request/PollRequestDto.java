package init.app.web.dto.request;

import init.app.web.dto.parent.PollDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
//@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PollRequestDto extends PollDto {

    @Size(min = 5, max = 512)
    private String question;
    @Size(min = 2, max = 50)
    private String firstChoice;
    @Size(min = 2, max = 50)
    private String secondChoice;
    @Size(min = 2, max = 50)
    private String thirdChoice;
}
