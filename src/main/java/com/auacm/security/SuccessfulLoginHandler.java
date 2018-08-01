package com.auacm.security;

import com.auacm.api.UserController;
import com.auacm.api.model.ResponseWrapper;
import com.auacm.api.model.response.MeResponse;
import com.auacm.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

@Component
public class SuccessfulLoginHandler implements AuthenticationSuccessHandler {
    @Autowired
    private UserController controller;

    @Autowired
    private JsonUtil jsonUtil;

    @Autowired
    private MappingJackson2HttpMessageConverter converter;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        MeResponse user = controller.login(httpServletRequest, httpServletResponse);
        converter.write(new ResponseWrapper(user, 200, "OK"), MediaType.APPLICATION_JSON_UTF8, new HttpOutputMessage() {
            @Override
            public OutputStream getBody() throws IOException {
                return httpServletResponse.getOutputStream();
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                httpServletResponse.getHeaderNames().forEach(name -> {
                    headers.addAll(name, new ArrayList<>(httpServletResponse.getHeaders(name)));
                });
                return headers;
            }
        });
//        httpServletResponse.setContentType("application/x-protobuf;charset=UTF-8");
//        httpServletResponse.setHeader("X-Protobuf-Message", "com.auacm.api.proto.MeResponseWrapper");
//        httpServletResponse.setHeader("X-Protobuf-Schema", "src/main/resources/proto/User.proto");
//        httpServletResponse.getOutputStream().write(response.toByteArray());
//        httpServletResponse.getOutputStream().close();
    }
}
