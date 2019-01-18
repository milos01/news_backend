package init.app.web.dto.request;

        import lombok.Data;

/**
 * Created by bojan.stankovic@codetri.be on 5/11/18.
 */
@Data
public class SignInRequestDto {

    private String email;
    private String password;

}
