package com.auacm.api.model;

public class JudgeFile<T> {
    private String fileName;
    private T data;

    public JudgeFile() {
    }

    public JudgeFile(String fileName, T data) {
        this.fileName = fileName;
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
