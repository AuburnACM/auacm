package io.github.auburnacm.auacm.database.service;

import io.github.auburnacm.auacm.api.model.CreateBlogPost;
import io.github.auburnacm.auacm.api.model.UpdateBlogPost;
import io.github.auburnacm.auacm.database.model.BlogPost;

import java.util.List;

public interface BlogPostService {
    BlogPost addBlogPost(BlogPost post);

    BlogPost addBlogPost(CreateBlogPost post, String username);

    BlogPost addBlogPost(String title, String subtitle, String body, String username);

    BlogPost updateBlogPost(BlogPost post);

    BlogPost updateBlogPost(UpdateBlogPost post, long id);

    void deleteBlogPost(BlogPost post);

    List<BlogPost> getAllBlogPosts();

    List<BlogPost> getBlogPostForUser(String username);

    BlogPost getBlogPostForId(long id);
}
