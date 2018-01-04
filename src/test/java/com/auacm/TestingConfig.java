package com.auacm;

import com.auacm.service.UserService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@Component
@Configuration
public class TestingConfig implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        UserService userService = event.getApplicationContext().getBean(UserService.class);
        userService.createUser("Admin", "admin", "password", true);
        userService.createUser("User", "user", "password", false);
    }

    @Bean
    public MockMvc getMockMvc(WebApplicationContext context) {
        return webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }
}
