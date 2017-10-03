package com.auacm.api;

import com.auacm.api.model.BlogPostResponse;
import com.auacm.api.model.CreateBlogPost;
import com.auacm.api.model.DataWrapper;
import com.auacm.api.model.UpdateBlogPost;
import com.auacm.api.validator.UpdateBlogPostValidator;
import com.auacm.database.model.BlogPost;
import com.auacm.database.model.User;
import com.auacm.database.model.UserPrincipal;
import com.auacm.database.service.BlogPostService;
import com.auacm.database.service.UserService;
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

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
public class BlogPostController {
    @Autowired
    private BlogPostService blogPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private UpdateBlogPostValidator updateBlogPostValidator;

    @Autowired
    private JsonUtil jsonUtil;

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
    public @ResponseBody String createBlogPost(@Validated @ModelAttribute CreateBlogPost blogPost) {
        User user = ((UserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        BlogPost post = blogPostService.addBlogPost(blogPost, user.getUsername());
        return new JsonFormat().printToString(blogPostService.getResponseForBlog(post, user));
    }

    @RequestMapping(path = "/api/blog", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody String getBlogPosts() {
        List<BlogPost> blogPosts = blogPostService.getAllBlogPosts();
        return jsonUtil.removeEmptyObjects(new JsonFormat().printToString(blogPostService.getResponseForBlogs(blogPosts)));
    }

    @RequestMapping(path = "/api/blog/{id}", produces = "application/json", method = {RequestMethod.GET})
    public @ResponseBody String getBlogPost(@PathVariable long id) {
        BlogPost blogPost = blogPostService.getBlogPostForId(id);
        return new JsonFormat().printToString(blogPostService.getResponseForBlog(blogPost));
    }

    @RequestMapping(path = "/api/blog/{id}", produces = "application/json",
            method = {RequestMethod.PUT, RequestMethod.POST})
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody String updateBlogPost(@Validated @ModelAttribute("updateBlogPost") UpdateBlogPost blogPost,
                                      @PathVariable long id) {
        BlogPost post = blogPostService.updateBlogPost(blogPost, id);
        return new JsonFormat().printToString(blogPostService.getResponseForBlog(post));
    }

    @RequestMapping(path = "/api/blog/{id}", produces = "application/json", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteBlogPost(@PathVariable long id) {
        return new JsonFormat().printToString(blogPostService.getResponseForBlog(blogPostService.deleteBlogPost(id)));
    }
}
