package init.app.domain.old_model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Id;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OldFooterPageOrder {

    @Id
    private UUID FooterPageOrderId;
    private Integer OrderNumber;
    private String Timestamp;
    private UUID ArticleRefID;
    private Boolean IsDeleted;
}
