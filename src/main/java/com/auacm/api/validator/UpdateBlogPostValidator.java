package com.auacm.api.validator;

import com.auacm.api.model.request.UpdateBlogPostRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UpdateBlogPostValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return UpdateBlogPostRequest.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        UpdateBlogPostRequest updateBlogPost = (UpdateBlogPostRequest) o;
        if (updateBlogPost.getBody() == null && updateBlogPost.getTitle() == null && updateBlogPost.getSubtitle() == null) {
            errors.reject("NoParameters");
        }
    }
}
