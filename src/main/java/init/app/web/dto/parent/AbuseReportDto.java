package init.app.web.dto.parent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class AbuseReportDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
}
