package com.auacm.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Proxy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Proxy(lazy = false)
@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails, Serializable {
    @Id
    @Column(name = "username")
    protected String username;

    @Column(name = "passw")
    protected String password;

    @Column(name = "display")
    protected String displayName;

    @JsonIgnore
    @Column(name = "admin")
    protected Boolean admin;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<BlogPost> blogPosts;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<CompetitionUser> competitionUsers;

    public User(String displayName, String username) {
        this.displayName = displayName;
        this.username = username;
    }

    public User(String username) {
        this.username = username;
    }

    public User(User user) {
        this.username = user.username;
        this.password = user.password;
        this.displayName = user.displayName;
        this.admin = user.admin;
        this.blogPosts = user.blogPosts;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO Implement permissions later
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        if (admin) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return authorities;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public String getPassword() {
        return this.password;
    }
}
