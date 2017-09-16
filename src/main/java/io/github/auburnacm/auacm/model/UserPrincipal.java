package io.github.auburnacm.auacm.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Mac on 9/14/17.
 */
public class UserPrincipal implements UserDetails {
    private User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO Implement permissions later
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        if (user.isAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        // TODO Implement later?
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO Implement later?
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Implement later?
        return true;
    }

    @Override
    public boolean isEnabled() {
        // TODO Implement later?
        return true;
    }

    public User getUser() {
        return user;
    }
}
