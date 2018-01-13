package com.auacm.service;

import com.auacm.api.proto.Blog;
import com.auacm.database.dao.BlogPostDao;
import com.auacm.database.model.BlogPost;
import com.auacm.api.model.CreateBlogPost;
import com.auacm.api.model.UpdateBlogPost;
import com.auacm.database.model.User;
import com.auacm.exception.BlogNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

@Service
public class BlogPostServiceImpl implements BlogPostService {

    @Autowired
    private BlogPostDao blogPostDao;

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public BlogPost addBlogPost(BlogPost post) {
        return blogPostDao.save(post);
    }

    @Override
    @Transactional
    public BlogPost addBlogPost(CreateBlogPost post, String username) {
        BlogPost newBlogPost = new BlogPost(post.getTitle(), post.getSubtitle(), post.getBody(), username);
        return addBlogPost(newBlogPost);

    }

    @Override
    @Transactional
    public BlogPost addBlogPost(String title, String subtitle, String body, String username) {
        BlogPost post = new BlogPost(title, subtitle, body, username);
        addBlogPost(post);
        return post;
    }

    @Override
    @Transactional
    public BlogPost updateBlogPost(BlogPost post) {
        return blogPostDao.saveAndFlush(post);
    }

    @Override
    @Transactional
    public BlogPost updateBlogPost(UpdateBlogPost post, long id) {
        BlogPost toUpdate = getBlogPostForId(id);
        if (toUpdate != null) {
            if (post.getBody() != null) {
                toUpdate.setBody(post.getBody());
            }
            if (post.getTitle() != null) {
                toUpdate.setTitle(post.getTitle());
            }
            if (post.getSubtitle() != null) {
                toUpdate.setSubtitle(post.getSubtitle());
            }
            updateBlogPost(toUpdate);
        }
        return toUpdate;
    }

    @Override
    @Transactional
    public BlogPost deleteBlogPost(BlogPost post) {
        blogPostDao.delete(post);
        return post;
    }

    @Override
    @Transactional
    public BlogPost deleteBlogPost(long postId) {
        BlogPost post = getBlogPostForId(postId);
        try {
            blogPostDao.delete(post);
        } catch (JpaObjectRetrievalFailureException e) {
            throw new BlogNotFoundException(String.format("Failed to delete a blog for id %d.", postId));
        }
        return post;
    }

    @Override
    public List<BlogPost> getAllBlogPosts() {
        try {
            List<BlogPost> posts = blogPostDao.findAll();
            Collections.sort(posts);
            return posts;
        } catch (JpaObjectRetrievalFailureException | EntityNotFoundException e) {
            throw new BlogNotFoundException("No blogs exist.");
        }
    }

    @Override
    public List<BlogPost> getBlogPostForUser(String username) {
        try {
            return blogPostDao.findByUsernameIgnoreCase(username);
        } catch (JpaObjectRetrievalFailureException | EntityNotFoundException e) {
            throw new BlogNotFoundException(String.format("Failed to find blogs for user %s.", username));
        }
    }

    @Override
    public List<BlogPost> getRecentBlogPostsForUser(String username, int amount) {
        return blogPostDao.findAllByUsernameOrderByPostTimeDesc(username, new PageRequest(0, amount));
    }

    @Override
    public BlogPost getBlogPostForId(long id) {
        try {
            return blogPostDao.getOne(id);
        } catch (JpaObjectRetrievalFailureException | EntityNotFoundException e) {
            throw new BlogNotFoundException(String.format("Failed to find a blog for id %d.", id));
        }
    }

    @Override
    public Blog.BlogResponseWrapper getResponseForBlog(BlogPost post, User user) {
        return Blog.BlogResponseWrapper.newBuilder().setData(Blog.BlogPostResponse.newBuilder()
                .setId(post.getId()).setBody(post.getBody()).setPostTime(post.getPostTime())
                .setSubtitle(post.getSubtitle()).setTitle(post.getTitle())
                .setAuthor(Blog.BlogPostResponse.Author.newBuilder().setDisplay(user.getDisplay())
                        .setUsername(user.getUsername()))).build();
    }

    @Override
    public Blog.BlogResponseWrapper getResponseForBlog(BlogPost post) {
        User user = userService.getUser(post.getUsername());
        return Blog.BlogResponseWrapper.newBuilder().setData(Blog.BlogPostResponse.newBuilder()
                .setId(post.getId()).setBody(post.getBody()).setPostTime(post.getPostTime())
                .setSubtitle(post.getSubtitle()).setTitle(post.getTitle())
                .setAuthor(Blog.BlogPostResponse.Author.newBuilder().setDisplay(user.getDisplay())
                        .setUsername(user.getUsername()))).build();
    }

    @Override
    public Blog.MultiPostWrapper getResponseForBlogs(List<BlogPost> posts) {
        Blog.MultiPostWrapper.Builder wrapper = Blog.MultiPostWrapper.newBuilder();
        if (posts.size() == 0) {
            wrapper.addData(Blog.BlogPostResponse.newBuilder());
        }
        for (BlogPost post : posts) {
            User user = userService.getUser(post.getUsername());
            wrapper.addData(Blog.BlogPostResponse.newBuilder()
                    .setId(post.getId()).setBody(post.getBody()).setPostTime(post.getPostTime())
                    .setSubtitle(post.getSubtitle()).setTitle(post.getTitle())
                    .setAuthor(Blog.BlogPostResponse.Author.newBuilder().setDisplay(user.getDisplay())
                            .setUsername(user.getUsername())));
        }
        return wrapper.build();
    }
}
