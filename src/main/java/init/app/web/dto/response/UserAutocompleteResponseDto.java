package init.app.web.dto.response;

import init.app.domain.model.enumeration.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by bojan.stankovic@codetri.be on 3/30/18.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAutocompleteResponseDto {

    private Long id;
    private String username;
    private String imageUrl;
    private Role role;

}
