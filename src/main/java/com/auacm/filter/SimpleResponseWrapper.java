package com.auacm.filter;

import org.apache.catalina.ssi.ByteArrayServletOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class SimpleResponseWrapper extends HttpServletResponseWrapper {
    private ByteArrayServletOutputStream outputStream;
    private ByteArrayOutputStream byteArrayOutputStream;
    private PrintWriter printWriter;
    private HashMap<String, List<String>> headers;

    /**
     * Creates a ServletResponse adaptor wrapping the given response object.
     *
     * @param response The response to wrap
     * @throws IllegalArgumentException if the response is null.
     */
    public SimpleResponseWrapper(HttpServletResponse response) {
        super(response);
        this.outputStream = new ByteArrayServletOutputStream();
        this.headers = new HashMap<>();
        this.byteArrayOutputStream = new ByteArrayOutputStream();
        this.printWriter = new PrintWriter(byteArrayOutputStream);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return printWriter;
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return headers.keySet();
    }

    @Override
    public void setHeader(String name, String value) {
        headers.put(name, Collections.singletonList(value));
    }

    @Override
    public void addHeader(String name, String value) {
        if (!headers.containsKey(name)) {
            headers.put(name, new ArrayList<>());
        }
        headers.get(name).add(value);
    }

    @Override
    public String getHeader(String name) {
        if (headers.get(name) != null) {
            return headers.get(name).get(0);
        } else {
            return null;
        }
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return headers.get(name);
    }

    @Override
    public boolean containsHeader(String name) {
        return headers.containsKey(name);
    }

    public void deleteHeader(String name) {
        headers.remove(name);
    }

    public byte[] getOutputArray() {
        return outputStream.toByteArray();
    }

    public byte[] getPrintWriterData() {
        return byteArrayOutputStream.toByteArray();
    }
}
