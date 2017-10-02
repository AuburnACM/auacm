package com.auacm.security;

import com.auacm.api.UserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

@Component
public class SuccessfulLoginHandler implements AuthenticationSuccessHandler {
    @Autowired
    private UserController controller;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        String response = controller.login(httpServletRequest, httpServletResponse);
        httpServletResponse.setContentType("application/json");
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(httpServletResponse.getOutputStream()));
        out.write(response);
        out.close();
    }
}
