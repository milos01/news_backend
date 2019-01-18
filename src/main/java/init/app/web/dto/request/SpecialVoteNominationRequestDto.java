package init.app.web.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by bojan.stankovic@codetri.be on 4/11/18.
 */
@Data
public class SpecialVoteNominationRequestDto {
    @Size(min = 2, max = 50)
    @NotNull
    private String specialVoteNomination;

}
