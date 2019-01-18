package init.app.web.dto.custom;

import lombok.Data;

/**
 * Created by bojan.stankovic@codetri.be on 7/5/18.
 */
@Data
public class EmailReminderDto {

    private String recipientEmail;
    private String planTextContent;
    private String htmlContent;
    private String username;
    private String url;

}
