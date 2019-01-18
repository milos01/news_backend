package init.app.domain.old_model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Id;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OldFollowing {

    @Id
    private UUID FollowingID;
    private UUID UserToFollowRefID;
    private UUID TagToFollowRefID;
    private UUID FollowingUserRefID;
    private String Timestamp;
    private Boolean IsDeleted;
    private Integer Type;
}
