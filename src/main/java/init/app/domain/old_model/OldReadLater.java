package init.app.domain.old_model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Id;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OldReadLater {

    @Id
    private UUID ReadLaterID;
    private UUID ArticleRefID;
    private UUID UserRefID;
    private String Timestamp;
    private String Note;
    private Boolean IsDeleted;
}
