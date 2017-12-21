package com.auacm.api.validator;

import com.auacm.api.model.NewProblem;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UpdateProblemValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return NewProblem.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        System.out.println("Here");
    }
}
