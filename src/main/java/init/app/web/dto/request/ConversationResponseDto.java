package init.app.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationResponseDto {

    public ConversationResponseDto(Long conversationId, String users, String userInfo, ZonedDateTime lastMessageTimestamp) {
        this.conversationId = conversationId;
        this.users = users;
        this.userInfo = userInfo;
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    private Long conversationId;
    private String users;
    private String userInfo;
    private ZonedDateTime lastMessageTimestamp;
    private Integer unreadMessages;

}
