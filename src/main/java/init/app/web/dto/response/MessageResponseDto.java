package init.app.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponseDto {

    private Long messageId;
    private String messageText;
    private Long userFromId;
    private String userFromUsername;
    private String userFromImageUrl;
    private ZonedDateTime messageTimestamp;

}
