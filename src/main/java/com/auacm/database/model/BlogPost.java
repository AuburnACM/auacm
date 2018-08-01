package com.auacm.database.model;

import com.auacm.api.model.request.CreateBlogPostRequest;
import com.auacm.api.model.request.UpdateBlogPostRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "blog_posts")
@Proxy(lazy = false)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BlogPost implements Serializable, Comparable<BlogPost> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    private String subtitle;

    @Column(name = "post_time")
    private long postTime;

    private String body;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "username")
    private User user;

    public BlogPost(String title, String subtitle, long postTime, String body, User user) {
        this.title = title;
        this.subtitle = subtitle;
        this.postTime = postTime;
        this.body = body;
        this.user = user;
    }

    public BlogPost(CreateBlogPostRequest request) {
        this.body = request.getBody();
        this.postTime = System.currentTimeMillis() / 1000;
        this.subtitle = request.getSubtitle();
        this.title = request.getTitle();
    }

    @Override
    public int compareTo(BlogPost o) {
        return Long.compare(o.getId(), id);
    }

    public void update(UpdateBlogPostRequest blogPostRequest) {
        if (blogPostRequest.getBody() != null) {
            this.body = blogPostRequest.getBody();
        }
        if (blogPostRequest.getTitle() != null) {
            this.title = blogPostRequest.getTitle();
        }
        if (blogPostRequest.getSubtitle() != null) {
            this.subtitle = blogPostRequest.getSubtitle();
        }
    }
}
