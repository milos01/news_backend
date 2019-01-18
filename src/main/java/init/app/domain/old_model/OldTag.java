package init.app.domain.old_model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Id;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OldTag {

    @Id
    private UUID TagID;
    private String Text;
    private String Timestamp;
    private Boolean IsDeleted;
}
