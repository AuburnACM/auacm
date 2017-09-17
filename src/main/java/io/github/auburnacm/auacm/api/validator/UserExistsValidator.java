package io.github.auburnacm.auacm.api.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by Mac on 9/16/17.
 */
public class UserExistsValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object o, Errors errors) {

    }
}
