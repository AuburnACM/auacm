package com.auacm.database.service;

import com.auacm.database.dao.BlogPostDao;
import com.auacm.database.model.BlogPost;
import com.auacm.api.model.CreateBlogPost;
import com.auacm.api.model.UpdateBlogPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

@Service
public class BlogPostServiceImpl implements BlogPostService {

    @Autowired
    private BlogPostDao blogPostDao;

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
    public void deleteBlogPost(BlogPost post) {
        blogPostDao.delete(post);
    }

    @Override
    @Transactional
    public List<BlogPost> getAllBlogPosts() {
        List<BlogPost> posts = blogPostDao.findAll();
        Collections.sort(posts);
        return posts;
    }

    @Override
    @Transactional
    public List<BlogPost> getBlogPostForUser(String username) {
        return blogPostDao.findByUsernameIgnoreCase(username);
    }

    @Override
    @Transactional
    public BlogPost getBlogPostForId(long id) {
        return blogPostDao.getOne(id);
    }
}
