package init.app.web.dto.custom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by bojan.stankovic@codetri.be on 9/24/18.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateContentNotificationDto {

    private Long contentId;
    private Long createdByUserId;

}
