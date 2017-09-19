package io.github.auburnacm.auacm.database.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "blog_posts")
public class BlogPost implements Serializable, Comparable<BlogPost> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    private String subtitle;

    @Column(name = "post_time")
    private long postTime;

    private String body;

    private String username;

    public BlogPost() {}

    public BlogPost(String title, String subtitle, String body, String username) {
        this.title = title;
        this.subtitle = subtitle;
        this.body = body;
        this.username = username;
        this.postTime = System.currentTimeMillis() / 1000;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int compareTo(BlogPost o) {
        return Long.compare(o.getId(), id);
    }
}
