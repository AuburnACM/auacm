package com.auacm.api.model;

/**
 * Created by Mac on 9/16/17.
 */
public class SimpleResponse {
    private long timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public SimpleResponse(int status, String message, String error, String path) {
        this.timestamp = System.currentTimeMillis();
        this.status = status;
        this.message = message;
        this.error = error;
        this.path = path;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
