package io.github.auburnacm.auacm.database.dao;

import io.github.auburnacm.auacm.database.model.BlogPost;

import java.util.List;

/**
 * Created by Mac on 9/17/17.
 */
public interface BlogPostDao {
    void addBlogPost(BlogPost object);

    List<BlogPost> getBlogPosts();

    List<BlogPost> getBlogPosts(String parameter, Object object);

    BlogPost getBlogPost(String parameter, Object object);

    void updateBlogPost(BlogPost object);

    void deleteBlogPost(BlogPost object);
}
