package com.auacm.api;

import com.auacm.security.AuthChannelInterceptorAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
    @Autowired
    private ConfigurableEnvironment environment;

    @Autowired
    private AuthChannelInterceptorAdaptor adaptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/competitions", "/login", "logout");
        config.setApplicationDestinationPrefixes("");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        boolean debug = environment.getProperty("sun.java.command") != null && environment.getProperty("sun.java.command").contains("--debug");
        StompWebSocketEndpointRegistration temp =  registry.addEndpoint("/api/ws");
        if (debug) {
            temp = temp.setAllowedOrigins("http://localhost:4200");
        }
        temp.withSockJS();
    }

    @Override
    public void configureClientInboundChannel(final ChannelRegistration registration) {
        registration.interceptors(adaptor);
    }
}
