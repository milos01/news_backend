package init.app.web.dto.request;

import lombok.Data;

import java.time.ZonedDateTime;

/**
 * Created by bojan.stankovic@codetri.be on 4/13/18.
 */
@Data
public class UnreadCounterDto {

    private Integer unreadCounter;
    private ZonedDateTime lastMessageTimestamp;

}
