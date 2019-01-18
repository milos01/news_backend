package init.app.domain.old_model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Id;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OldPoll {

    @Id
    private UUID PollID;
    private String Question;
    private String FirstChoiceText;
    private String SecondChoiceText;
    private String ThirdChoiceText;
    private Integer R_FirstChoiceAmount;
    private Integer R_SecondChoiceAmount;
    private Integer R_ThirdChoiceAmount;
    private UUID ArticleRefID;
    private String Timestamp;
    private Boolean IsDeleted;
    private Boolean IsPollOFTheDay;
}
