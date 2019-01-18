package init.app.exception;

import com.jcabi.aspects.Loggable;
import init.app.web.dto.parent.ExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Loggable(trim = false, prepend = true)
@ControllerAdvice
public class CustomExceptionHandler {

    final static public String DEFAULT_EXCEPTION = "Oops, something went wrong!";

    @ExceptionHandler(value = {Exception.class, CustomException.class})
    public static ResponseEntity handleException(Exception exception) {
        if (exception.getClass().equals(CustomException.class)) {
            return handleCustomException((CustomException) exception, ((CustomException) exception).getHttpStatus());
        } else {
            return new ResponseEntity<>(new ExceptionDto(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static ResponseEntity handleCustomException(CustomException customException, HttpStatus httpStatus) {
        return new ResponseEntity<>(new ExceptionDto(customException.getMessage()), customException.getHttpStatus() != null ? customException.getHttpStatus() : httpStatus);
    }
}
