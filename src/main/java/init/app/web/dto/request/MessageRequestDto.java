package init.app.web.dto.request;

import init.app.web.dto.parent.MessageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MessageRequestDto extends MessageDto {

    private List<Long> usersToList;
    private Long conversationId;

}
