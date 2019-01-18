package init.app.domain.old_model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Id;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OldMedia {

    @Id
    private UUID MediaID;
    private String ServerPath;
    private UUID ArticleRefID;
    private String Timestamp;
    private String Description;
    private Boolean IsDeleted;
    private Integer OrdinalNumber;
}
