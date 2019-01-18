package init.app.domain.old_model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Id;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OldUser {

    @Id
    private UUID UserID;
    private String Email;
    private String Password;
    private String UserName;
    private String BIO;
    private String FacebookId;
    private String GoogleId;
    private String TwitterId;
    private String ProfilePictureServerPath;
    private String Timestamp;
    private Integer Type;
    private Boolean IsDeleted;
    private Boolean IsDeactivated;
    private String EmailActivatedTimestamp;
}