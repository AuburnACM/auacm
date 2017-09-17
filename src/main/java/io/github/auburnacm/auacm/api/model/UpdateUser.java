package io.github.auburnacm.auacm.api.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class UpdateUser {

    @Min(2)
    @Max(32)
    private String username;

    @Min(2)
    @Max(32)
    private String display;

    private boolean admin;

    @Min(8)
    @Max(255)
    private String oldPassword;

    @Min(8)
    @Max(255)
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

    public boolean isAdmin() {
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
