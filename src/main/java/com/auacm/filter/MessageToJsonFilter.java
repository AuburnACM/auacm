package com.auacm.filter;

import com.auacm.Auacm;
import com.auacm.api.proto.CompetitionOuterClass;
import com.auacm.util.JsonUtil;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class MessageToJsonFilter extends GenericFilterBean {
    private List<Class> messages;

    @Autowired
    private JsonUtil jsonUtil;

    public MessageToJsonFilter(ListableBeanFactory listableBeanFactory) {
        messages = new ArrayList<>();
        Map<String, Object> controllers = listableBeanFactory.getBeansWithAnnotation(Controller.class);
        controllers.putAll(listableBeanFactory.getBeansWithAnnotation(RestController.class));
        for (Object o : controllers.values()) {
            Class c = o.getClass();
            for (Method m : c.getDeclaredMethods()) {
                if (GeneratedMessageV3.class.isAssignableFrom(m.getReturnType())) {
                    if (!messages.contains(m.getReturnType())) {
                        messages.add(m.getReturnType());
                    }
                }
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (response instanceof HttpServletResponse) {
            SimpleResponseWrapper responseWrapper = new SimpleResponseWrapper((HttpServletResponse) response);
            chain.doFilter(request, responseWrapper);
            HttpServletResponse servletResponse = (HttpServletResponse) responseWrapper.getResponse();
            if (responseWrapper.getHeader("Content-Type") != null
                    && responseWrapper.getHeader("Content-Type").contains("application/x-protobuf;")) {
                String json = parseMessage(responseWrapper.getOutputArray(),
                        responseWrapper.getHeader("X-Protobuf-Message"));
                servletResponse.getOutputStream().write(json.getBytes());
                servletResponse.setHeader("Content-Type", "application/json;charset=UTF-8");
            } else {
                if (responseWrapper.containsHeader("Content-Type")) {
                    servletResponse.setHeader("Content-Type", responseWrapper.getHeader("Content-Type"));
                }
                servletResponse.getOutputStream().write(responseWrapper.getOutputArray());
            }
        }
    }

    private String parseMessage(byte[] data, String type) {
        String[] split = type.split("\\.");
        for (Class c : messages) {
            try {
                if (c.getSimpleName().equals(split[split.length - 1])) {
                    Method method = c.getDeclaredMethod("parseFrom", byte[].class);
                    Message message = (Message) method.invoke(null, data);
                    return jsonUtil.toJson(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        return "";
    }
}
