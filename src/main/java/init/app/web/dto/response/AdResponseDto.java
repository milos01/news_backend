package init.app.web.dto.response;

import init.app.domain.model.Ad;
import init.app.domain.model.enumeration.AdType;
import init.app.web.dto.parent.AdDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdResponseDto extends AdDto {

    private Long id;
    private String href;
    private String imageUrl;
    private AdType type;
    private ZonedDateTime createTime;
}
