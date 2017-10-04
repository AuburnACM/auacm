package com.auacm.api.validator;

import com.auacm.api.model.CreateUser;
import com.auacm.database.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CreateUserValidator implements Validator {

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return CreateUser.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        CreateUser user = (CreateUser)o;
        if (userService.getUser(user.getUsername()) != null) {
            errors.reject("User already exists!");
        }
    }
}
