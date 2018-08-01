package com.auacm.api.validator;

import com.auacm.database.model.BlogPost;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CreateBlogPostValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return BlogPost.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        BlogPost blogPost = (BlogPost) o;
        if (blogPost.getTitle() == null || StringUtils.isEmpty(blogPost.getTitle())) {
            errors.rejectValue("title", "Title cannot be null or empty");
        }
        if (blogPost.getSubtitle() == null || StringUtils.isEmpty(blogPost.getSubtitle())) {
            errors.rejectValue("subtitle", "Subtitle cannot be null or empty");
        }
        if (blogPost.getBody() == null || StringUtils.isEmpty(blogPost.getBody())) {
            errors.rejectValue("body", "Title cannot be null or empty");
        }
    }
}
