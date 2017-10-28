package com.auacm.api.model;

import com.auacm.database.model.BlogPost;
import com.auacm.database.model.User;

public class BlogPostResponse {
    private long id;

    private String title;

    private String subtitle;

    private long postTime;

    private String body;

    private AuthorResponse author;

    public BlogPostResponse() {}

    public BlogPostResponse(BlogPost blogPost, User user) {
        this.id = blogPost.getId();
        this.title = blogPost.getTitle();
        this.subtitle = blogPost.getSubtitle();
        this.postTime = blogPost.getPostTime();
        this.body = blogPost.getBody();
        this.author = new AuthorResponse(user.getUsername(), user.getDisplay());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public long getPostTime() {
        return postTime;
    }

    public void setPostTime(long postTime) {
        this.postTime = postTime;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public AuthorResponse getAuthor() {
        return author;
    }

    public void setAuthor(AuthorResponse author) {
        this.author = author;
    }
}
