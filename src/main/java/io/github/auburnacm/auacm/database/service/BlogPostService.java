package io.github.auburnacm.auacm.database.service;

import io.github.auburnacm.auacm.api.model.CreateBlogPost;
import io.github.auburnacm.auacm.api.model.UpdateBlogPost;
import io.github.auburnacm.auacm.database.model.BlogPost;

import java.util.List;

public interface BlogPostService {
    void addBlogPost(BlogPost post);

    void addBlogPost(CreateBlogPost post, String username);

    void addBlogPost(String title, String subtitle, String body, String username);

    void updateBlogPost(BlogPost post);

    void updateBlogPost(UpdateBlogPost post, long id);

    void deleteBlogPost(BlogPost post);

    List<BlogPost> getAllBlogPosts();

    List<BlogPost> getBlogPostForUser(String username);

    BlogPost getBlogPostForId(long id);
}
