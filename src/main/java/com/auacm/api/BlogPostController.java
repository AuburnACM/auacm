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
import com.auacm.exception.ProtobufParserException;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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

    @InitBinder(value = "updateBlogPost")
    protected void initBinder(final WebDataBinder binder) {
        binder.addValidators(updateBlogPostValidator);
    }

    @RequestMapping(path = "/api/blog", produces = "application/json", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody String createBlogPost(@Validated @ModelAttribute CreateBlogPost blogPost) {
        User user = ((UserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        BlogPost post = blogPostService.addBlogPost(blogPost, user.getUsername());
        String output = "";
        try {
            JsonFormat.parser().merge(output, blogPostService.getResponseForBlog(post, user).toBuilder());
            return output;
        } catch (InvalidProtocolBufferException e) {
            throw new ProtobufParserException(e);
        }
    }

    @RequestMapping(path = "/api/blog", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody DataWrapper<List<BlogPostResponse>> getBlogPosts(HttpServletResponse response) {
        List<BlogPost> blogPosts = blogPostService.getAllBlogPosts();
        List<BlogPostResponse> responseList = new ArrayList<>();
        for (BlogPost blogPost : blogPosts) {
            User user = userService.getUser(blogPost.getUsername());
            if (user != null) {
                responseList.add(new BlogPostResponse(blogPost, user));
            }
        }
        return new DataWrapper<>(responseList, response.getStatus());
    }

    @RequestMapping(path = "/api/blog/{id}", produces = "application/json", method = {RequestMethod.GET})
    public @ResponseBody DataWrapper<BlogPostResponse> getBlogPost(@PathVariable long id, HttpServletResponse response) {
        BlogPost blogPost = blogPostService.getBlogPostForId(id);
        User user = userService.getUser(blogPost.getUsername());
        if (user != null) {
            return new DataWrapper<>(new BlogPostResponse(blogPost, user), response.getStatus());
        } else {
            throw new UsernameNotFoundException(String.format("Failed to find user %s for blog post %d!", blogPost.getUsername(), id));
        }
    }

    @RequestMapping(path = "/api/blog/{id}", produces = "application/json",
            method = {RequestMethod.PUT, RequestMethod.POST})
    public DataWrapper<BlogPostResponse> updateBlogPost(@Validated @ModelAttribute("updateBlogPost") UpdateBlogPost blogPost,
                                      @PathVariable long id, HttpServletResponse response) {

        BlogPost post = blogPostService.updateBlogPost(blogPost, id);
        User user = userService.getUser(post.getUsername());
        return new DataWrapper<>(new BlogPostResponse(post, user), response.getStatus());
    }
}
