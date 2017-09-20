package io.github.auburnacm.auacm.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.auburnacm.auacm.api.model.*;
import io.github.auburnacm.auacm.api.validator.UpdateBlogPostValidator;
import io.github.auburnacm.auacm.api.validator.UpdateUserValidator;
import io.github.auburnacm.auacm.database.model.User;
import io.github.auburnacm.auacm.database.model.UserPrincipal;
import io.github.auburnacm.auacm.database.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UpdateUserValidator updateUserValidator;

    @InitBinder("updateUser")
    protected void initBinder(final WebDataBinder binder) {
        binder.addValidators(updateUserValidator);
    }

    @RequestMapping(value = "/api/login", produces = "application/json", method = {RequestMethod.POST, RequestMethod.GET})
    public String login(HttpServletRequest request, HttpServletResponse response) {
        if (response.getStatus() == 200) {
            JsonObject object = new JsonObject();
            JsonArray perms = new JsonArray();
            if (SecurityContextHolder.getContext().getAuthentication() == null
                    || SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
                throw new BadCredentialsException("Invalid username or password!");
            }
            Collection<? extends GrantedAuthority> authorityCollection = SecurityContextHolder.getContext()
                    .getAuthentication().getAuthorities();
            for (GrantedAuthority g : authorityCollection) {
                perms.add(g.getAuthority());
            }
            object.add("permissions", perms);
            return object.toString();
        }
        return "{}";
    }

    @RequestMapping(value = "/api/logout", produces = "application/json", method = {RequestMethod.POST, RequestMethod.GET})
    public String logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return "";
    }

    @RequestMapping(value = "/api/me", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody DataWrapper me(HttpServletResponse response) {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principal.getUser();
        List<String> permissions = new ArrayList<>();
        for (GrantedAuthority g : principal.getAuthorities()) {
            permissions.add(g.getAuthority());
        }
        MeResponse me = new MeResponse(user.getUsername(), user.getDisplay(), user.isAdmin() ? 1 : 0, permissions);
        return new DataWrapper(me, response.getStatus());
    }

    @RequestMapping(value = "/api/create_user", produces = "application/json", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SimpleResponse> createUser(@Validated @ModelAttribute CreateUser user) {
        User user1 = userService.getUser(user.getUsername());
        if (user1 == null) {
            userService.createUser(user.getDisplay(), user.getUsername(), user.getPassword(), user.isAdmin());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SimpleResponse(HttpStatus.BAD_REQUEST.value(),
                    "User already exists!", "UsernameAlreadyExists", "/api/create_user"));
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/api/change_password", produces = "application/json", method = RequestMethod.POST)
    public String changePassword(@Validated @ModelAttribute UpdateUser user) {
        User userInstance = (User) ((UserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        if (user.getNewPassword() != null) {
            userService.updatePassword(userInstance, user.getNewPassword());
        }
        return "";
    }

    @RequestMapping(value = "/api/update_user", produces = "application/json", method = RequestMethod.POST)
    public String updateUser(@Validated @ModelAttribute("updateUser") UpdateUser user) {
        User userInstance = (User) ((UserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        if (user.getNewPassword() != null) {
            userInstance.setPassword(user.getNewPassword());
        }
        if (user.getDisplay() != null) {
            userInstance.setDisplay(user.getDisplay());
        }
        // TODO Enable this when we don't use usernames as primary keys
//        if (user.getUsername() != null) {
//            userInstance.setUsername(user.getUsername());
//        }
        userService.updateUser(userInstance);
        return "";
    }

    @RequestMapping(value = "/api/ranking", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody DataWrapper getRanks(HttpServletResponse response) {
        return getRanks("all", response);
    }

    @RequestMapping(value = "/api/ranking/{timeFrame}", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody DataWrapper getRanks(@PathVariable String timeFrame, HttpServletResponse response) {
        if (timeFrame == null) {
            timeFrame = "all";
        }
        return new DataWrapper(userService.getRanks(timeFrame), response.getStatus());
    }
}
