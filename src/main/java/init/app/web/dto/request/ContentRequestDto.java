package init.app.web.dto.request;

import init.app.domain.model.enumeration.ContentType;
import init.app.web.dto.parent.ContentDto;
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
public class ContentRequestDto extends ContentDto {

    @Size(min = 3, max = 100)
    private String headline;

    @Size(max = 50000)
    private String text;

    private Long categoryId;

    @NotNull
    private Boolean published;
}
