package com.auacm.api.model;

import javax.validation.constraints.Size;

public class UpdateUser {

    @Size(min = 2, max = 255)
    private String username;

    @Size(min = 2, max = 255)
    private String display;

    private Boolean admin;

    @Size(min = 8, max = 255)
    private String oldPassword;

    @Size(min = 8, max = 255)
    private String newPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public Boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
