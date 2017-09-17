package io.github.auburnacm.auacm.database.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "passw")
    private String password;

    @Column(name = "display")
    private String display;

    @Column(name = "admin")
    private boolean admin;

    @OneToMany(targetEntity = Submission.class, fetch = FetchType.LAZY)
    private List<Submission> submissions;

    @OneToMany(targetEntity = BlogPost.class, fetch = FetchType.LAZY)
    private List<BlogPost> blogPosts;

    public User() {
        this.username = "";
        this.password = "";
        this.display = "";
        this.admin = false;
        submissions = new ArrayList<Submission>();
        blogPosts = new ArrayList<BlogPost>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }

    public List<BlogPost> getBlogPosts() {
        return blogPosts;
    }

    public void setBlogPosts(List<BlogPost> blogPosts) {
        this.blogPosts = blogPosts;
    }
}
