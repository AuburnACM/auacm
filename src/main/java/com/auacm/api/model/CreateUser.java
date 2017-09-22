package com.auacm.api.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class CreateUser {

    @NotNull
    @Min(2)
    @Max(32)
    private String username;

    @NotNull
    @Min(8)
    @Max(255)
    private String password;

    @NotNull
    @Min(2)
    @Max(32)
    private String display;

    private boolean admin = false;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
