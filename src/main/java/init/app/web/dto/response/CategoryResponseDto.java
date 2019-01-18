package init.app.web.dto.response;

import init.app.web.dto.parent.CategoryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CategoryResponseDto extends CategoryDto {

    @NotNull
    private Long id;

    public CategoryResponseDto(String text, Long id) {
        super(text);
        this.id = id;
    }
}
