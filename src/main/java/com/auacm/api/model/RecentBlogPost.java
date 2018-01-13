package com.auacm.api.model;

import com.auacm.database.model.BlogPost;

public class RecentBlogPost {
    private String title;
    private String subtitle;
    private long postTime;
    private long id;

    public RecentBlogPost() {}

    public RecentBlogPost(BlogPost blogPost) {
        this.id = blogPost.getId();
        this.postTime = blogPost.getPostTime();
        this.title = blogPost.getTitle();
        this.subtitle = blogPost.getSubtitle();
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
