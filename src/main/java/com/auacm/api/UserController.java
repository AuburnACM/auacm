package com.auacm.api;

import com.auacm.api.model.CreateUser;
import com.auacm.api.model.UpdateUser;
import com.auacm.api.validator.CreateUserValidator;
import com.auacm.api.validator.UpdateUserValidator;
import com.auacm.database.model.User;
import com.auacm.database.model.UserPrincipal;
import com.auacm.exception.UserException;
import com.auacm.service.FileSystemService;
import com.auacm.service.UserService;
import com.auacm.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UpdateUserValidator updateUserValidator;

    @Autowired
    private CreateUserValidator createUserValidator;

    @Autowired
    private JsonUtil jsonUtil;

    @Autowired
    private FileSystemService fileSystemService;

    @InitBinder("createUser")
    private void initCreateUserValidator(WebDataBinder binder) {
        binder.addValidators(createUserValidator);
    }

    @InitBinder("updateUser")
    protected void initBinder(final WebDataBinder binder) {
        binder.addValidators(updateUserValidator);
    }

    private Logger logger;

    public UserController() {
        logger = LoggerFactory.getLogger(UserController.class);
    }

    @RequestMapping(value = "/api/login", method = {RequestMethod.POST, RequestMethod.GET})
    public com.auacm.api.proto.User.MeResponseWrapper login(HttpServletRequest request, HttpServletResponse response) {
        if (response.getStatus() == 200) {
            if (SecurityContextHolder.getContext().getAuthentication() == null
                    || SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
                throw new BadCredentialsException("Invalid username or password!");
            }
            return userService
                    .getMeResponse((UserPrincipal)SecurityContextHolder
                            .getContext().getAuthentication().getPrincipal());
        }
        throw new BadCredentialsException("Invalid username or password!");
    }

    @RequestMapping(value = "/api/logout", method = {RequestMethod.POST, RequestMethod.GET})
    public void logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
    }

    @RequestMapping(value = "/api/me", method = RequestMethod.GET)
    public @ResponseBody com.auacm.api.proto.User.MeResponseWrapper me() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getMeResponse(principal);
    }

    @RequestMapping(value = "/api/create_user", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody com.auacm.api.proto.User.MeResponseWrapper
    createUser(@Validated @ModelAttribute("createUser") CreateUser user) {
        User user1 = userService.createUser(user.getDisplay(), user.getUsername(), user.getPassword(), user.isAdmin());
        return userService.getMeResponse(new UserPrincipal(user1));
    }

    @RequestMapping(value = "/api/change_password", method = RequestMethod.POST)
    public void changePassword(@Validated @ModelAttribute("updateUser") UpdateUser user) {
        User userInstance = ((UserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        if (user.getNewPassword() != null) {
            userService.updatePassword(userInstance, user.getNewPassword());
        } else {
            throw new UserException("Password is empty!");
        }
    }

    @RequestMapping(value = "/api/update_user", produces = "application/json", method = RequestMethod.POST)
    public void updateUser(@Validated @ModelAttribute("updateUser") UpdateUser user) {
        userService.updateSelf(user);
    }

    @RequestMapping(value = "/api/ranking", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody
    com.auacm.api.proto.User.RankResponseWrapper getRanks() {
        return getRanks("all");
    }

    @RequestMapping(value = "/api/ranking/{timeFrame}", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody
    com.auacm.api.proto.User.RankResponseWrapper getRanks(@PathVariable String timeFrame) {
        if (timeFrame == null) {
            timeFrame = "all";
        }
        return userService.getRankedResponse(userService.getRanks(timeFrame));
    }

    @RequestMapping(value = "/api/profile/image/{username}", method = RequestMethod.GET)
    public ResponseEntity<Resource> getProfilePicture(@PathVariable String username) {
        return new ResponseEntity<>(fileSystemService.getProfilePicture(username), HttpStatus.OK);
    }


}
