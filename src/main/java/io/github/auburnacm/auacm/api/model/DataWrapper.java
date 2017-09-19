package io.github.auburnacm.auacm.api.model;

public class DataWrapper {
    private Object data;
    private int status;

    public DataWrapper(Object object, int status) {
        this.data = object;
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
