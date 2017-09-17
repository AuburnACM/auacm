package io.github.auburnacm.auacm.api.model;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class CreateUser {

    @NotNull
    @Length(min = 2, max = 32)
    private String username;

    @NotNull
    @Length(min = 8, max = 255)
    private String password;

    @NotNull
    @Length(min = 2, max = 32)
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
