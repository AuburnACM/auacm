package com.auacm.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class CorsConfig {

    @Autowired
    private ConfigurableEnvironment environment;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        boolean debug = environment.getProperty("sun.java.command") != null && environment.getProperty("sun.java.command").contains("--debug");
        if (debug) {
            return new WebMvcConfigurerAdapter() {
                @Override
                public void addCorsMappings(CorsRegistry registry) {
                    registry.addMapping("/api/**")
                            .allowedMethods("GET", "POST", "DELETE", "PUT", "OPTIONS")
                            .allowedOrigins("http://localhost:4200")
//                            .allowedHeaders("Content-Type", "X-Requested-With", "accept", "Origin", "Access-Control-Request-Method",
//                                    "Access-Control-Request-Headers")
                            .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
                            .allowCredentials(true).maxAge(3600);
                }
            };
        } else {
            return new WebMvcConfigurerAdapter() {
            };
        }
    }
}
