package io.github.auburnacm.auacm.database.service;

import io.github.auburnacm.auacm.api.model.CreateBlogPost;
import io.github.auburnacm.auacm.api.model.UpdateBlogPost;
import io.github.auburnacm.auacm.database.dao.BlogPostDao;
import io.github.auburnacm.auacm.database.model.BlogPost;
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
    public void addBlogPost(BlogPost post) {
        blogPostDao.addBlogPost(post);
    }

    @Override
    @Transactional
    public void addBlogPost(CreateBlogPost post, String username) {
        BlogPost newBlogPost = new BlogPost(post.getTitle(), post.getSubtitle(), post.getBody(), username);
        addBlogPost(newBlogPost);
    }

    @Override
    @Transactional
    public void addBlogPost(String title, String subtitle, String body, String username) {
        BlogPost post = new BlogPost(title, subtitle, body, username);
        addBlogPost(post);
    }

    @Override
    @Transactional
    public void updateBlogPost(BlogPost post) {
        blogPostDao.updateBlogPost(post);
    }

    @Override
    @Transactional
    public void updateBlogPost(UpdateBlogPost post, long id) {
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
    }

    @Override
    @Transactional
    public void deleteBlogPost(BlogPost post) {
        blogPostDao.deleteBlogPost(post);
    }

    @Override
    @Transactional
    public List<BlogPost> getAllBlogPosts() {
        List<BlogPost> posts = blogPostDao.getBlogPosts();
        Collections.sort(posts);
        return posts;
    }

    @Override
    @Transactional
    public List<BlogPost> getBlogPostForUser(String username) {
        return blogPostDao.getBlogPosts("username", username);
    }

    @Override
    @Transactional
    public BlogPost getBlogPostForId(long id) {
        return blogPostDao.getBlogPost("id", id);
    }
}
