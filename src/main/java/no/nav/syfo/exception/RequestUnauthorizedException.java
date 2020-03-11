package no.nav.syfo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class RequestUnauthorizedException extends RuntimeException {
    public RequestUnauthorizedException(String message) {
        super(message);
    }
}
