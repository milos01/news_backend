package init.app.component;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface Responsable<T> {

    ResponseEntity response(T body);

    ResponseEntity response(Object object, HttpStatus httpStatus);

    ResponseEntity response(HttpStatus httpStatus);

    ResponseEntity responseNoContent();

    ResponseEntity responseCreated();
}
