package io.github.auburnacm.auacm.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.auburnacm.auacm.api.model.CreateUser;
import io.github.auburnacm.auacm.api.model.SimpleResponse;
import io.github.auburnacm.auacm.database.service.UserService;
import io.github.auburnacm.auacm.model.User;
import io.github.auburnacm.auacm.model.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collection;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/api/login", produces = "application/json", method = {RequestMethod.POST, RequestMethod.GET})
    public String login(HttpServletResponse response) {
        if (response.getStatus() == 200) {
            JsonObject object = new JsonObject();
            JsonArray perms = new JsonArray();
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                throw new BadCredentialsException("Invalid username or password!");
            }
            Collection<? extends GrantedAuthority> authorityCollection = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
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
    public String me(HttpServletResponse response) {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principal.getUser();
        JsonObject object = new JsonObject();
        object.add("username", new JsonPrimitive(user.getUsername()));
        object.add("displayName", new JsonPrimitive(user.getDisplay()));
        object.add("isAdmin", new JsonPrimitive(user.isAdmin() ? 1 : 0));
        JsonArray permissions = new JsonArray();
        for (GrantedAuthority g : principal.getAuthorities()) {
            permissions.add(g.getAuthority());
        }
        object.add("permissions", permissions);
        return object.toString();
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
}
