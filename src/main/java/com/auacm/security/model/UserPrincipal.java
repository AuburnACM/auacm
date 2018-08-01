package com.auacm.security.model;

import com.auacm.database.model.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

public class UserPrincipal extends User {

    public UserPrincipal() {
    }

    public UserPrincipal(String displayName, String username) {
        super(displayName, username);
    }

    public UserPrincipal(User user) {
        super(user);
    }

    @Override
    @JsonProperty
    public Boolean getAdmin() {
        return admin;
    }

    @JsonProperty
    public Collection<? extends String> getPermissions() {
        return super.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
    }
}
