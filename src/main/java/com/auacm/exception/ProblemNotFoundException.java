package com.auacm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Problem not found")
public class ProblemNotFoundException extends RuntimeException {
    public ProblemNotFoundException() {
    }

    public ProblemNotFoundException(String message) {
        super(message);
    }

    public ProblemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProblemNotFoundException(Throwable cause) {
        super(cause);
    }

    public ProblemNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
