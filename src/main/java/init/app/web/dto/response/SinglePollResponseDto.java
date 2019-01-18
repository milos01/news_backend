package init.app.web.dto.response;

import init.app.web.dto.parent.PollDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SinglePollResponseDto extends PollResponseDto {

    private Boolean voted;
    private String nomination;

}
