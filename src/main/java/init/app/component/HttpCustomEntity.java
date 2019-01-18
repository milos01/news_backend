package init.app.component;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static org.springframework.http.HttpStatus.*;

@Component
public class HttpCustomEntity<T> implements Responsable<T> {

    public ResponseEntity response(T body) {
        return body == null ? responseNoContent() : response(body, OK);
    }

    public ResponseEntity response(Object object, HttpStatus httpStatus) {
        return new ResponseEntity<>(object, httpStatus);
    }

    public ResponseEntity response(HttpStatus httpStatus) {
        return new ResponseEntity<>(httpStatus);
    }

    public ResponseEntity responseNoContent() {
        return new ResponseEntity<>(NO_CONTENT);
    }

    public ResponseEntity responseCreated() {
        return new ResponseEntity<>(CREATED);
    }
}
