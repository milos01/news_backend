package init.app.web.dto.custom;

import init.app.web.dto.response.MessageResponseDto;
import lombok.Data;

/**
 * Created by bojan.stankovic@codetri.be on 4/12/18.
 */
@Data
public class MessageNotificationDto extends MessageResponseDto {

    private Long conversationId;

}
