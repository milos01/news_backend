package init.app.domain.old_model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Id;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OldShare {

    @Id
    private UUID ShareID;
    private UUID SharingUserRefID;
    private UUID ArticleToShareRefID;
    private String Timestamp;
    private Boolean IsDeleted;
}
