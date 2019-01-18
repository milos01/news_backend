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
public class PollResponseDto extends PollDto {

    private Long id;
    private String question;
    private String firstChoice;
    private String secondChoice;
    private String thirdChoice;
    private Integer firstAmount;
    private Integer secondAmount;
    private Integer thirdAmount;
    private ZonedDateTime createTime;
    private Boolean isDeleted;
}
