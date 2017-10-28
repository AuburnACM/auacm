package com.auacm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Failed to parse protobuf.")
public class ProtobufParserException extends RuntimeException{
    public ProtobufParserException() {
    }

    public ProtobufParserException(String message) {
        super(message);
    }

    public ProtobufParserException(Throwable cause) {
        super(cause);
    }
}
