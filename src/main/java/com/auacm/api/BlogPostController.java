package com.auacm.api;

import com.auacm.api.model.request.CreateBlogPostRequest;
import com.auacm.api.model.request.UpdateBlogPostRequest;
import com.auacm.api.model.response.BlogPostListResponse;
import com.auacm.api.model.response.BlogPostResponse;
import com.auacm.api.validator.UpdateBlogPostValidator;
import com.auacm.database.model.BlogPost;
import com.auacm.service.BlogPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class BlogPostController {
    private final BlogPostService blogPostService;

    private final UpdateBlogPostValidator updateBlogPostValidator;

    @RequestMapping(path = "/api/blog", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded", produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody BlogPostResponse createBlogPost(@Validated @ModelAttribute CreateBlogPostRequest blogPost) {
        return new BlogPostResponse(blogPostService.addBlogPost(blogPost));
    }

    @RequestMapping(path = "/api/blog", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody BlogPostListResponse getBlogPosts(@RequestParam(required = false) Integer limit,
                                      @RequestParam(required = false) Integer page) {
        return new BlogPostListResponse(blogPostService.getAllBlogPosts(
                limit == null ? 10 : limit, page == null ? 0 : page));
    }

    @RequestMapping(path = "/api/blog/{id}", method = {RequestMethod.GET}, produces = "application/json")
    public @ResponseBody BlogPostResponse getBlogPost(@PathVariable long id) {
        return new BlogPostResponse(blogPostService.getBlogPostForId(id));
    }

    @RequestMapping(path = "/api/blog/{id}", method = {RequestMethod.PUT, RequestMethod.POST})
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody BlogPostResponse updateBlogPost(@ModelAttribute UpdateBlogPostRequest blogPost,
                                                 @PathVariable long id, BindingResult bindingResult)
            throws MethodArgumentNotValidException, NoSuchMethodException {
        updateBlogPostValidator.validate(blogPost, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new MethodArgumentNotValidException(new MethodParameter(this.getClass()
                    .getDeclaredMethod("updateBlogPost", BlogPost.class, BindingResult.class), 0),
                    bindingResult);
        }
        return new BlogPostResponse(blogPostService.updateBlogPost(blogPost, id));
    }

    @RequestMapping(path = "/api/blog/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBlogPost(@PathVariable long id) {
        blogPostService.deleteBlogPost(id);
    }
}
