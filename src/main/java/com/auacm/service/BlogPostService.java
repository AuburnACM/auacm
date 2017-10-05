package com.auacm.service;

import com.auacm.api.model.CreateBlogPost;
import com.auacm.api.proto.Blog;
import com.auacm.database.model.BlogPost;
import com.auacm.api.model.UpdateBlogPost;
import com.auacm.database.model.User;

import java.util.List;

public interface BlogPostService {
    BlogPost addBlogPost(BlogPost post);

    BlogPost addBlogPost(CreateBlogPost post, String username);

    BlogPost addBlogPost(String title, String subtitle, String body, String username);

    BlogPost updateBlogPost(BlogPost post);

    BlogPost updateBlogPost(UpdateBlogPost post, long id);

    BlogPost deleteBlogPost(BlogPost post);

    BlogPost deleteBlogPost(long postId);

    List<BlogPost> getAllBlogPosts();

    List<BlogPost> getBlogPostForUser(String username);

    BlogPost getBlogPostForId(long id);

    Blog.BlogResponseWrapper getResponseForBlog(BlogPost post, User user);

    Blog.BlogResponseWrapper getResponseForBlog(BlogPost post);

    Blog.MultiPostWrapper getResponseForBlogs(List<BlogPost> posts);
}
