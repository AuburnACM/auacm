package com.auacm.api.model.response;

import com.auacm.database.model.BlogPost;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BlogPostListResponse extends ArrayList<BlogPostResponse> {
    public BlogPostListResponse(int initialCapacity) {
        super(initialCapacity);
    }

    public BlogPostListResponse() {
    }

    public BlogPostListResponse(Collection<? extends BlogPostResponse> c) {
        super(c);
    }

    public BlogPostListResponse(List<? extends BlogPost> c) {
        super();
        c.forEach(blogPost -> this.add(new BlogPostResponse(blogPost)));
    }
}
