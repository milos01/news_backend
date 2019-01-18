package init.app.web.dto.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PageResponseDto extends AllResponseDto {

    int totalPages;
    int requestSize;
    int requestPage;
    int numberOfElements;
    boolean isLast;
    boolean isFirst;
}
