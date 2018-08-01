package com.auacm.service;

import com.auacm.api.model.request.CreateBlogPostRequest;
import com.auacm.api.model.request.UpdateBlogPostRequest;
import com.auacm.database.dao.BlogPostDao;
import com.auacm.database.model.BlogPost;
import com.auacm.database.model.User;
import com.auacm.exception.BlogNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class BlogPostServiceImpl implements BlogPostService {

    @Autowired
    private BlogPostDao blogPostDao;

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public BlogPost addBlogPost(CreateBlogPostRequest blogPostRequest) {
        BlogPost newBlogPost = new BlogPost(blogPostRequest);
        if (blogPostRequest.getUsername() == null) {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            newBlogPost.setUser(user);
        } else {
            User user = userService.getUser(blogPostRequest.getUsername());
            newBlogPost.setUser(user);
        }
        return blogPostDao.save(newBlogPost);
    }

    @Override
    @Transactional
    public BlogPost addBlogPost(String title, String subtitle, String body, String username) {
        CreateBlogPostRequest newBlogPost = new CreateBlogPostRequest(title, subtitle, body, username);
        return addBlogPost(newBlogPost);
    }

    @Override
    @Transactional
    public BlogPost updateBlogPost(BlogPost post) {
        return blogPostDao.saveAndFlush(post);
    }

    @Override
    @Transactional
    public BlogPost updateBlogPost(UpdateBlogPostRequest updateBlogPostRequest, long id) {
        BlogPost toUpdate = getBlogPostForId(id);
        toUpdate.update(updateBlogPostRequest);
        blogPostDao.save(toUpdate);
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
    public List<BlogPost> getAllBlogPosts(int limit, int page) {
        try {
            PageRequest request = PageRequest.of(page, limit, new Sort(Sort.Direction.DESC, "id"));
            return blogPostDao.findAll(request).getContent();
        } catch (JpaObjectRetrievalFailureException | EntityNotFoundException e) {
            throw new BlogNotFoundException("No blogs exist.");
        }
    }

    @Override
    public List<BlogPost> getBlogPostForUser(String username) {
        try {
            return blogPostDao.findByUserUsernameIgnoreCase(username);
        } catch (JpaObjectRetrievalFailureException | EntityNotFoundException e) {
            throw new BlogNotFoundException(String.format("Failed to find blogs for user %s.", username));
        }
    }

    @Override
    public List<BlogPost> getRecentBlogPostsForUser(String username, int amount) {
        return blogPostDao.findAllByUserUsernameOrderByPostTimeDesc(username, PageRequest.of(0, amount));
    }

    @Override
    @Transactional
    public BlogPost getBlogPostForId(long id) {
        Optional<BlogPost> blogPost = blogPostDao.findById(id);
        if (blogPost.isPresent()) {
            return blogPost.get();
        } else {
            throw new BlogNotFoundException(String.format("Failed to find a blog for id %d.", id));
        }
    }
}
