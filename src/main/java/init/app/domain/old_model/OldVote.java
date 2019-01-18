package init.app.domain.old_model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Id;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OldVote {

    @Id
    private UUID VoteID;
    private UUID UserRefID;
    private UUID PollRefID;
    private Boolean IsFirstChoice;
    private Boolean IsSecondChoice;
    private Boolean IsThirdChoice;
    private String Timestamp;
    private Boolean IsDeleted;
}
