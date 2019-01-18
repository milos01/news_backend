package init.app.web.dto.parent;

import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
}
