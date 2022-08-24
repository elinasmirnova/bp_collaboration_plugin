package cz.cvut.felk.kbss.freeplane.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Class of custom API response status exceptions.
 */
public class ApiResponseStatusException extends ResponseStatusException {

    public ApiResponseStatusException(HttpStatus status) {
        super(status);
    }

    public ApiResponseStatusException(HttpStatus status, String reason) {
        super(status, reason);
    }

    public ApiResponseStatusException(HttpStatus status, String reason, Throwable cause) {
        super(status, reason, cause);
    }

    public ApiResponseStatusException(int rawStatusCode, String reason, Throwable cause) {
        super(rawStatusCode, reason, cause);
    }

    @Override
    public String getMessage() {
        return super.getReason();
    }
}
