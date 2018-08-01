package com.auacm.api.validator;

import com.auacm.api.model.Submission;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class SubmissionValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return Submission.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Submission submission = (Submission) o;
        if (submission.getFile() == null) {
            errors.reject("The submission file cannot be null!");
        }
        if (submission.getPid() == null) {
            errors.reject("The problem pid cannot be null!");
        }
    }
}
