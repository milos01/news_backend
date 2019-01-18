package init.app.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by bojan.stankovic@codetri.be on 4/3/18.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FooterPageCreateResponseDto {

    private Long footerPageId;
    private Long contentId;

}
