package com.auacm.security;

import com.auacm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Autowired
    private SuccessfulLoginHandler handler;

    @Autowired
    private FailedLoginHandler failedLoginHandler;

    @Override
    protected void configure(HttpSecurity security) throws Exception {
        security.exceptionHandling().authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                .and().authorizeRequests()
                .antMatchers("/api/me", "/api/create_user",
                        "/api/change_password", "/api/update_user").fullyAuthenticated()
                .antMatchers(HttpMethod.POST, "/api/blog").fullyAuthenticated()
                .antMatchers(HttpMethod.PUT, "/api/blog/**").fullyAuthenticated()
                // Enable if we want to support http basic
//                .and().httpBasic().authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                .and().formLogin().loginPage("/api/login").successHandler(handler).failureHandler(failedLoginHandler).permitAll()
                .and().csrf().disable();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new BCryptAuthenticationProvider(userService));
    }
}
