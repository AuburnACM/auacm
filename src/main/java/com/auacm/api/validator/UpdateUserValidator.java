package com.auacm.api.validator;

import com.auacm.database.model.UserPrincipal;
import com.auacm.api.model.UpdateUser;
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
        return UpdateUser.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User userInstance = (User) ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        UpdateUser user = (UpdateUser) o;
        if (user.getOldPassword() != null) {
            boolean valid = userService.validatePassword(userInstance.getUsername(), user.getOldPassword());
            if (!valid) {
                errors.reject("oldPassword", "PasswordMissmatch");
            }
        }

        if (user.getUsername() == null && user.getDisplay() == null && user.getOldPassword() == null
                && user.getNewPassword() == null && user.isAdmin() == null) {
            errors.reject("NoParameters");
        }
    }
}
