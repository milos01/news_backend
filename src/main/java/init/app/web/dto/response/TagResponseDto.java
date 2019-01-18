package init.app.web.dto.response;

import init.app.domain.model.enumeration.TagType;
import init.app.web.dto.parent.TagDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TagResponseDto extends TagDto{
    private Long id;
    private String text;
    private TagType type;
    private Integer rFollowers;
    private Boolean following;
}
