package com.auacm.model;

import com.auacm.api.model.CreateBlogPost;

public class MockBlogPostBuilder {
    private CreateBlogPost blogPost;

    public MockBlogPostBuilder() {
        blogPost = new CreateBlogPost();
        blogPost.setBody("Test Body");
        blogPost.setTitle("Test Title");
        blogPost.setSubtitle("Test Subtitle");
    }

    public MockBlogPostBuilder setBody(String body) {
        this.blogPost.setSubtitle(body);
        return this;
    }

    public MockBlogPostBuilder setTitle(String title) {
        this.blogPost.setTitle(title);
        return this;
    }

    public MockBlogPostBuilder setSubtitle(String subtitle) {
        this.blogPost.setSubtitle(subtitle);
        return this;
    }

    public CreateBlogPost build() {
        return blogPost;
    }
}
