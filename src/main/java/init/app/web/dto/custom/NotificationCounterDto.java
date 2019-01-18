package init.app.web.dto.custom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by bojan.stankovic@codetri.be on 3/15/18.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationCounterDto {

    private Integer unreadCounter;

}
