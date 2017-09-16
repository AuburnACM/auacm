package io.github.auburnacm.auacm.security;

import io.github.auburnacm.auacm.database.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
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
                .antMatchers("/api/me").fullyAuthenticated()
                .and().httpBasic()
                .and().formLogin().loginPage("/api/login")
                .and().logout().clearAuthentication(true).logoutUrl("/api/logout")
                .and().csrf().disable();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new BCryptAuthenticationProvider(userService));
    }
}
