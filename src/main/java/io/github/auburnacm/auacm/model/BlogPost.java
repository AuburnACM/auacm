package io.github.auburnacm.auacm.model;

import javax.persistence.*;

/**
 * Created by Mac on 9/13/17.
 */
@Entity
@Table(name = "blog_posts")
public class BlogPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    private String subtitle;

    @Column(name = "post_time")
    private long postTime;

    private String body;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    private String username;
}
