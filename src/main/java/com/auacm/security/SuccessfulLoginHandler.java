package com.auacm.security;

import com.auacm.api.UserController;
import com.auacm.api.proto.User;
import com.auacm.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
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

    @Autowired
    private JsonUtil jsonUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        User.MeResponseWrapper response = controller.login(httpServletRequest, httpServletResponse);
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(httpServletResponse.getOutputStream()));
        out.write(jsonUtil.toJson(response));
//        httpServletResponse.setContentType("application/x-protobuf;charset=UTF-8");
//        httpServletResponse.setHeader("X-Protobuf-Message", "com.auacm.api.proto.MeResponseWrapper");
//        httpServletResponse.setHeader("X-Protobuf-Schema", "src/main/resources/proto/User.proto");
//        httpServletResponse.getOutputStream().write(response.toByteArray());
//        httpServletResponse.getOutputStream().close();
        out.close();
    }
}
