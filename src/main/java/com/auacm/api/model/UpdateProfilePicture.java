package com.auacm.api.model;

import javax.validation.constraints.NotNull;

public class UpdateProfilePicture {
    @NotNull
    private String data;

    private String mimeType;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
