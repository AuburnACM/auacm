package com.auacm.database.model;

import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Proxy(lazy = false)
public class User implements Serializable {

    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "passw")
    private String password;

    @Column(name = "display")
    private String display;

    @Column(name = "admin")
    private boolean admin;

    public User() {
        this.username = "";
        this.password = "";
        this.display = "";
        this.admin = false;
    }

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
