package com.auacm.api.model;

import java.util.List;

public class RegisterUsers {
    private List<String> userNames;

    public List<String> getUserNames() {
        return userNames;
    }

    public void setUserNames(List<String> userNames) {
        this.userNames = userNames;
    }
}
