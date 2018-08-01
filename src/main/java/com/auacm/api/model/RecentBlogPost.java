package com.auacm.api.model;

import com.auacm.database.model.BlogPost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RecentBlogPost {
    private String title;
    private String subtitle;
    private long postTime;
    private long id;

    public RecentBlogPost(BlogPost blogPost) {
        this.id = blogPost.getId();
        this.postTime = blogPost.getPostTime();
        this.title = blogPost.getTitle();
        this.subtitle = blogPost.getSubtitle();
    }
}
