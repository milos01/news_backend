package init.app.web.dto.response;

import init.app.domain.model.enumeration.Role;
import init.app.web.dto.parent.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CommentResponseDto extends CommentDto {
    private Long id;
    private String text;
    private String url;
    private Long user_id;
    private Integer r_replies;
    private String r_user_image_url;
    private String r_username;
    private Role r_user_role;
    private ZonedDateTime createTime;
    private ZonedDateTime updateTime;


}
