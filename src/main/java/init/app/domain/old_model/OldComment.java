package init.app.domain.old_model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Id;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OldComment {

    @Id
    private UUID CommentID;
    private UUID AuthorRefID;
    private String Text;
    private UUID ArticleRefID;
    private UUID CommentRefID;
    private Boolean IsCommentOnComment;
    private Boolean IsCommentOnArticle;
    private String Timestamp;
    private Boolean IsDeleted;
    private String PictureServerPath;
}
