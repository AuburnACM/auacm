package com.auacm.api.model;

public class DataWrapper<T> {
    private T data;
    private int status;

    public DataWrapper(T object, int status) {
        this.data = object;
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
