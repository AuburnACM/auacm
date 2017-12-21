package com.auacm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Problem pdf not found.")
public class PdfNotFoundException extends RuntimeException {
    public PdfNotFoundException() {
    }

    public PdfNotFoundException(String message) {
        super(message);
    }

    public PdfNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PdfNotFoundException(Throwable cause) {
        super(cause);
    }

    public PdfNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
