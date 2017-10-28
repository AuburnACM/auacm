package com.auacm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(reason = "Failed to find a blog post.", value = HttpStatus.NOT_FOUND)
public class BlogNotFoundException extends RuntimeException {
    public BlogNotFoundException() {
    }

    public BlogNotFoundException(String message) {
        super(message);
    }

    public BlogNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BlogNotFoundException(Throwable cause) {
        super(cause);
    }
}
