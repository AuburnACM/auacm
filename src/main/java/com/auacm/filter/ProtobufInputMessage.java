package com.auacm.filter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProtobufInputMessage implements HttpInputMessage {
    private InputStream inputStream;
    private HttpHeaders headers;

    public ProtobufInputMessage(byte[] data, HttpHeaders headers) {
        inputStream = new ByteArrayInputStream(data);
        this.headers = headers;
    }

    @Override
    public InputStream getBody() throws IOException {
        return inputStream;
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }
}
