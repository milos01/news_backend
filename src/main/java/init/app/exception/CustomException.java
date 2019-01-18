package init.app.exception;

import com.jcabi.aspects.Loggable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Loggable(trim = false, prepend = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class CustomException extends Exception {

    private HttpStatus httpStatus = HttpStatus.CONFLICT;

    public CustomException(String message) {
        super(message);
    }

    public CustomException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
