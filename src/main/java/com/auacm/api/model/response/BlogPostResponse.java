package com.auacm.api.model.response;

import com.auacm.api.model.BasicUser;
import com.auacm.database.model.BlogPost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BlogPostResponse {
    private BasicUser author;
    private String body;
    private Long postTime;
    private String subtitle;
    private String title;

    public BlogPostResponse(BlogPost blogPost) {
        this.author = new BasicUser(blogPost.getUser());
        this.body = blogPost.getBody();
        this.postTime = blogPost.getPostTime();
        this.subtitle = blogPost.getSubtitle();
        this.title = blogPost.getTitle();
    }
}
