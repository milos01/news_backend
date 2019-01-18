package init.app.domain.old_model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Id;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OldUserTag {

    @Id
    private UUID UserTagID;
    private UUID TagRefID;
    private UUID UserRefID;
    private String Timestamp;
    private Boolean IsDeleted;
}
