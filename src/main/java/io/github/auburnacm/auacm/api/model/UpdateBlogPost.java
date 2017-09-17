package io.github.auburnacm.auacm.api.model;

import javax.validation.constraints.Size;

public class UpdateBlogPost {
    @Size(min = 1)
    private String title;

    @Size(min = 1)
    private String subtitle;

    @Size(min = 1)
    private String body;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
