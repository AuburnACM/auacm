package com.auacm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(reason = "Failed to find competition.", value = HttpStatus.NOT_FOUND)
public class CompetitionNotFoundException extends RuntimeException {
    public CompetitionNotFoundException() {
    }

    public CompetitionNotFoundException(String message) {
        super(message);
    }

    public CompetitionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CompetitionNotFoundException(Throwable cause) {
        super(cause);
    }

    public CompetitionNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
