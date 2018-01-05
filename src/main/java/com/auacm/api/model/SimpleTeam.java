package com.auacm.api.model;

public class SimpleTeam {
    private String display;
    private String username;

    public SimpleTeam() {}

    public SimpleTeam(String display, String username) {
        this.display = display;
        this.username = username;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SimpleTeam) {
            return display.equals(((SimpleTeam) o).display) && username.equals(((SimpleTeam) o).username);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (display + username).hashCode();
    }
}
