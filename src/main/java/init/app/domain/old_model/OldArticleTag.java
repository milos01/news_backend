package init.app.domain.old_model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Id;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OldArticleTag {

    @Id
    private UUID ArticleTagID;
    private UUID TagRefID;
    private UUID ArticleRefID;
    private String Timestamp;
    private Boolean IsDeleted;
}
