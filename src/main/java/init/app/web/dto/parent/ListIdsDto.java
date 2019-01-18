package init.app.web.dto.parent;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
//@NoArgsConstructor
@EqualsAndHashCode
public class ListIdsDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @NonNull
    private List<Long> ids;
}
