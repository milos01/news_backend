package init.app.web.dto.response;

import init.app.domain.model.enumeration.FooterPageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Created by bojan.stankovic@codetri.be on 4/3/18.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FooterPageResponseDto {

    private Long footerPageId;
    private FooterPageType type;
    private Long contentId;
    private String headline;
    private String text;
    private String rMediaUrl;
    private Integer orderNumber;
    private ZonedDateTime createTime;

}
