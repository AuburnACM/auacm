package io.github.auburnacm.auacm.api.model;

public class AuthorResponse {
    private String username;

    private String display;

    public AuthorResponse() {}

    public AuthorResponse(String username, String display) {
        this.username = username;
        this.display = display;
    }

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
}
