package init.app.web.dto.request;

import init.app.domain.model.enumeration.AdType;
import init.app.web.dto.parent.AdDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
//@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AdRequestDto extends AdDto {

    @Size(min = 5, max = 120)
    @NotNull
    private String href;
    @NotNull
    private AdType type;

}
