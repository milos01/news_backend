package init.app.domain.old_model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Id;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OldArticle {

    @Id
    private UUID ArticleID;
    private UUID AuthorRefID;
    private Integer Type;
    private String Text;
    private String Headline;
    private String Timestamp;
    private Boolean IsPublished;
    private String PublishedAt;
    private Integer Activity;
    private Boolean IsDeleted;
    private String ArticleVideoLink;
    private Integer TempActivity;
    private Integer SponsoredActivity;
}
