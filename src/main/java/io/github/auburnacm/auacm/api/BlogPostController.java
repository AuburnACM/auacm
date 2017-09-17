package io.github.auburnacm.auacm.api;

import io.github.auburnacm.auacm.api.model.BlogPostResponse;
import io.github.auburnacm.auacm.api.model.CreateBlogPost;
import io.github.auburnacm.auacm.api.model.UpdateBlogPost;
import io.github.auburnacm.auacm.api.validator.UpdateBlogPostValidator;
import io.github.auburnacm.auacm.database.model.BlogPost;
import io.github.auburnacm.auacm.database.model.User;
import io.github.auburnacm.auacm.database.model.UserPrincipal;
import io.github.auburnacm.auacm.database.service.BlogPostService;
import io.github.auburnacm.auacm.database.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class BlogPostController {
    @Autowired
    private BlogPostService blogPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private UpdateBlogPostValidator updateBlogPostValidator;

    @InitBinder
    protected void initBinder(final WebDataBinder binder) {
        binder.addValidators(updateBlogPostValidator);
    }

    @RequestMapping(path = "/api/blog", produces = "application/json", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public String createBlogPost(@Validated @ModelAttribute("createBlogPost") CreateBlogPost blogPost) {
        User user = ((UserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        blogPostService.addBlogPost(blogPost, user.getUsername());
        return "{ \"response\": \"success\" }";
    }

    @RequestMapping(path = "/api/blog", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody List<BlogPostResponse> getBlogPosts() {
        List<BlogPost> blogPosts = blogPostService.getAllBlogPosts();
        List<BlogPostResponse> responseList = new ArrayList<>();
        for (BlogPost blogPost : blogPosts) {
            User user = userService.getUser(blogPost.getUsername());
            if (user != null) {
                responseList.add(new BlogPostResponse(blogPost, user));
            }
        }
        return responseList;
    }

    @RequestMapping(path = "/api/blog/{id}", produces = "application/json", method = {RequestMethod.GET})
    public @ResponseBody BlogPostResponse getBlogPost(@PathVariable long id) {
        BlogPost blogPost = blogPostService.getBlogPostForId(id);
        User user = userService.getUser(blogPost.getUsername());
        if (user != null) {
            return new BlogPostResponse(blogPost, user);
        } else {
            throw new UsernameNotFoundException(String.format("Failed to find user %s for blog post %d!", blogPost.getUsername(), id));
        }
    }

    @RequestMapping(path = "/api/blog/{id}", produces = "application/json", method = {RequestMethod.PUT, RequestMethod.POST})
    public String updateBlogPost(@Validated @ModelAttribute("updateBlogPost") UpdateBlogPost blogPost, @PathVariable String id) {
        blogPostService.updateBlogPost(blogPost, Long.parseLong(id));
        return "{ \"response\": \"success\" }";
    }
}
