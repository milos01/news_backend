package init.app.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Created by bojan.stankovic@codetri.be on 3/7/18.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserFollowResponseDto extends UserResponseDto {

    private Boolean following;

}
