package io.github.auburnacm.auacm.database.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "blog_posts")
public class BlogPost implements Serializable {
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
