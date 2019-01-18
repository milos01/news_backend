package init.app.web.dto.request;

import init.app.domain.model.enumeration.FooterPageType;
import init.app.web.dto.parent.FooterPageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FooterPageRequestDto extends FooterPageDto {

    private ContentRequestDto content;
    private Integer order;
    private FooterPageType type;

}
