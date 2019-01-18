package init.app.web.dto.custom;

import init.app.web.dto.shared.GenericResponseDto;
import lombok.Data;

import java.util.List;

/**
 * Created by bojan.stankovic@codetri.be on 6/5/18.
 */
@Data
public class MessageCreationResponseDto {

    private GenericResponseDto apiResponse;

    private MessageNotificationDto messageNotification;

    private ConversationCounterNotificationDto conversationNotification;

    private Boolean newConversationCreated;

    private List<Long> usersToList;

}
