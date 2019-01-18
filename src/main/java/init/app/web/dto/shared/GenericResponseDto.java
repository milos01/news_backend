package init.app.web.dto.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    Object content;
}
