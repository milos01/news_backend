package init.app.web.dto.request;

import lombok.Data;

import java.time.ZonedDateTime;

/**
 * Created by bojan.stankovic@codetri.be on 4/16/18.
 */
@Data
public class UnreadMessagesCounterDto {

    private Integer unreadCounter;
    private ZonedDateTime lastMessageTimestamp;

}
