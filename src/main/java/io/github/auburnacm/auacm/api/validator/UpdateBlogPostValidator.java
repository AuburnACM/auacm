package io.github.auburnacm.auacm.api.validator;

import io.github.auburnacm.auacm.api.model.UpdateBlogPost;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UpdateBlogPostValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return UpdateBlogPost.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        UpdateBlogPost updateBlogPost = (UpdateBlogPost) o;
        if (updateBlogPost.getBody() == null && updateBlogPost.getTitle() == null && updateBlogPost.getSubtitle() == null) {
            errors.reject("NoParameters");
        }
    }
}
