package com.auacm.security;

import com.auacm.database.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Override
    protected void configure(HttpSecurity security) throws Exception {
        security.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/problems").permitAll()
                .antMatchers("/problem/**").permitAll()
                .antMatchers("/rankings").permitAll()
                .antMatchers("/competitions").permitAll()
                .antMatchers("/competition/**").permitAll()
                .antMatchers("/profile/**").permitAll()
                .antMatchers("/rankings").permitAll()
                .antMatchers("/api/hash").permitAll()
                .antMatchers("/api/ranking", "/api/ranking/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/blog", "/api/blog/**").permitAll()
                .and().authorizeRequests()
                .antMatchers("/api/me", "/api/create_user",
                        "/api/change_password", "/api/update_user").fullyAuthenticated()
                .antMatchers(HttpMethod.POST, "/api/blog").fullyAuthenticated()
                .antMatchers(HttpMethod.PUT, "/api/blog/**").fullyAuthenticated()
                .and().httpBasic()
                .and().formLogin().loginPage("/api/login").permitAll()
                .and().csrf().disable();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new BCryptAuthenticationProvider(userService));
    }
}
