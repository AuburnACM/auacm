package com.auacm.api.validator;

import com.auacm.api.model.request.UpdateUserRequest;
import com.auacm.database.model.User;
import com.auacm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UpdateUserValidator implements Validator{
    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return UpdateUserRequest.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User userInstance = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UpdateUserRequest user = (UpdateUserRequest) o;
        if (user.getPassword() != null) {
            boolean valid = userService.validatePassword(userInstance.getUsername(), user.getPassword());
            if (!valid) {
                errors.reject("oldPassword", "PasswordMissmatch");
            }
        }

        if (user.getUsername() == null && user.getDisplayName() == null && user.getPassword() == null
                && user.getNewPassword() == null && user.getAdmin() == null) {
            errors.reject("NoParameters");
        }
    }
}
