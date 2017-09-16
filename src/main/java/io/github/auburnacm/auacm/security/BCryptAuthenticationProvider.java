package io.github.auburnacm.auacm.security;

import io.github.auburnacm.auacm.database.service.UserService;
import io.github.auburnacm.auacm.model.User;
import io.github.auburnacm.auacm.model.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class BCryptAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private UserService userService;

    public BCryptAuthenticationProvider(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
        String username = usernamePasswordAuthenticationToken.getName().trim();
        String password = usernamePasswordAuthenticationToken.getCredentials().toString().trim();
        if (!userService.userExists(username)) {
            throw new BadCredentialsException("Invalid username or password!");
        }
        if (!userService.validatePassword(username, password)) {
            throw new BadCredentialsException("Invalid username or password!");
        }
    }

    @Override
    protected UserDetails retrieveUser(String s, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
        User user = userService.getUser(usernamePasswordAuthenticationToken.getName());
        if (user == null) {
            throw new UsernameNotFoundException("Failed to locate a user with that username!");
        }
        return new UserPrincipal(user);
    }
}
