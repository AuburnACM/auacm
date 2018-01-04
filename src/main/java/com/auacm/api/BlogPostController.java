package com.auacm.api;

import com.auacm.api.model.CreateBlogPost;
import com.auacm.api.model.UpdateBlogPost;
import com.auacm.api.proto.Blog;
import com.auacm.api.validator.UpdateBlogPostValidator;
import com.auacm.database.model.BlogPost;
import com.auacm.database.model.User;
import com.auacm.database.model.UserPrincipal;
import com.auacm.service.BlogPostService;
import com.auacm.service.UserService;
import com.auacm.util.JsonUtil;
import com.googlecode.protobuf.format.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BlogPostController {
    @Autowired
    private BlogPostService blogPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private UpdateBlogPostValidator updateBlogPostValidator;

    private Logger logger;

    public BlogPostController() {
        logger = LoggerFactory.getLogger(BlogPostController.class);
    }

    @InitBinder(value = "updateBlogPost")
    protected void initBinder(final WebDataBinder binder) {
        binder.addValidators(updateBlogPostValidator);
    }

    @RequestMapping(path = "/api/blog", produces = "application/json", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody Blog.BlogResponseWrapper createBlogPost(@Validated @ModelAttribute CreateBlogPost blogPost) {
        User user = ((UserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        BlogPost post = blogPostService.addBlogPost(blogPost, user.getUsername());
        return blogPostService.getResponseForBlog(post, user);
    }

    @RequestMapping(path = "/api/blog", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody Blog.MultiPostWrapper getBlogPosts() {
        List<BlogPost> blogPosts = blogPostService.getAllBlogPosts();
        return blogPostService.getResponseForBlogs(blogPosts);
    }

    @RequestMapping(path = "/api/blog/{id}", produces = "application/json", method = {RequestMethod.GET})
    public @ResponseBody Blog.BlogResponseWrapper getBlogPost(@PathVariable long id) {
        BlogPost blogPost = blogPostService.getBlogPostForId(id);
        return blogPostService.getResponseForBlog(blogPost);
    }

    @RequestMapping(path = "/api/blog/{id}", produces = "application/json",
            method = {RequestMethod.PUT, RequestMethod.POST})
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody Blog.BlogResponseWrapper updateBlogPost(
            @Validated @ModelAttribute("updateBlogPost") UpdateBlogPost blogPost, @PathVariable long id) {
        BlogPost post = blogPostService.updateBlogPost(blogPost, id);
        return blogPostService.getResponseForBlog(post);
    }

    @RequestMapping(path = "/api/blog/{id}", produces = "application/json", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ADMIN')")
    public Blog.BlogResponseWrapper deleteBlogPost(@PathVariable long id) {
        return blogPostService.getResponseForBlog(blogPostService.deleteBlogPost(id));
    }
}
