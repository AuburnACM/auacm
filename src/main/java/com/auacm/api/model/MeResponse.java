package com.auacm.api.model;

import java.util.List;

public class MeResponse {
    private String username;

    private String displayName;

    private int isAdmin;

    private List<String> permissions;

    public MeResponse() {}

    public MeResponse(String username, String displayName, int isAdmin, List<String> permissions) {
        this.username = username;
        this.displayName = displayName;
        this.isAdmin = isAdmin;
        this.permissions = permissions;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
