package com.auacm.service;

import com.auacm.api.model.request.CreateBlogPostRequest;
import com.auacm.api.model.request.UpdateBlogPostRequest;
import com.auacm.database.model.BlogPost;

import java.util.List;

public interface BlogPostService {
    BlogPost addBlogPost(CreateBlogPostRequest post);

    BlogPost addBlogPost(String title, String subtitle, String body, String username);

    BlogPost updateBlogPost(BlogPost post);

    BlogPost updateBlogPost(UpdateBlogPostRequest post, long id);

    BlogPost deleteBlogPost(BlogPost post);

    BlogPost deleteBlogPost(long postId);

    List<BlogPost> getAllBlogPosts(int limit, int page);

    List<BlogPost> getBlogPostForUser(String username);

    List<BlogPost> getRecentBlogPostsForUser(String username, int amount);

    BlogPost getBlogPostForId(long id);
}
