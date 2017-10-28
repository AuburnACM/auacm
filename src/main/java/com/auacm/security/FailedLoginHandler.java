package com.auacm.security;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

@Component
public class FailedLoginHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse,
                                        AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setStatus(403);
        httpServletResponse.setContentType("application/json");
        JsonObject object = new JsonObject();
        object.add("timestamp", new JsonPrimitive(System.currentTimeMillis()));
        object.add("status", new JsonPrimitive(403));
        object.add("error", new JsonPrimitive("Invalid username or password!"));
        object.add("exception", new JsonPrimitive("BadCredentialsException"));
        object.add("message", new JsonPrimitive("Invalid username or password!"));
        object.add("path", new JsonPrimitive("/api/login"));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(httpServletResponse.getOutputStream()));
        out.write(object.toString());
        out.close();
    }
}
