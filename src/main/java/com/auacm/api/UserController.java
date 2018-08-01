package com.auacm.api;

import com.auacm.api.model.request.UpdateUserRequest;
import com.auacm.api.model.response.MeResponse;
import com.auacm.api.model.response.RankedUserListResponse;
import com.auacm.api.validator.CreateUserValidator;
import com.auacm.api.validator.UpdateUserValidator;
import com.auacm.database.model.User;
import com.auacm.service.FileSystemService;
import com.auacm.service.UserService;
import com.auacm.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping(value = "/api/login", method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json")
    public MeResponse login(HttpServletRequest request, HttpServletResponse response) {
        if (response.getStatus() == 200) {
            if (SecurityContextHolder.getContext().getAuthentication() == null
                    || SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
                throw new BadCredentialsException("Invalid username or password!");
            }
            return new MeResponse((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
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

    @RequestMapping(value = "/api/me", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody MeResponse me() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new MeResponse(user);
    }

    @RequestMapping(value = "/api/create_user", method = RequestMethod.POST, produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody MeResponse createUser(@Validated @ModelAttribute("createUser") User user) {
        return new MeResponse(userService.createUser(user.getDisplayName(),
                user.getUsername(), user.getPassword(), user.getAdmin()));
    }

    @RequestMapping(value = "/api/change_password", method = RequestMethod.POST)
    public void changePassword(@Validated @ModelAttribute("updateUser") UpdateUserRequest user) {
        User userInstance = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.updatePassword(userInstance, user.getNewPassword());
    }

    @RequestMapping(value = "/api/update_user", produces = "application/json", method = RequestMethod.POST)
    public void updateUser(@Validated @ModelAttribute("updateUser") UpdateUserRequest user) {
        userService.updateSelf(user);
    }

    @RequestMapping(value = "/api/ranking", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody RankedUserListResponse getRanks() {
        return getRanks("all");
    }

    @RequestMapping(value = "/api/ranking/{timeFrame}", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody RankedUserListResponse getRanks(@PathVariable String timeFrame) {
        if (timeFrame == null) {
            timeFrame = "all";
        }
        return new RankedUserListResponse(userService.getRanks(timeFrame));
    }
}
