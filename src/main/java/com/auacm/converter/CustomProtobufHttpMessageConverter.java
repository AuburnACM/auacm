package com.auacm.converter;

import com.auacm.util.JsonUtil;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import com.google.protobuf.TextFormat;
import com.googlecode.protobuf.format.HtmlFormat;
import com.googlecode.protobuf.format.JsonFormat;
import com.googlecode.protobuf.format.ProtobufFormatter;
import com.googlecode.protobuf.format.XmlFormat;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CustomProtobufHttpMessageConverter extends ProtobufHttpMessageConverter {

    private static final ProtobufFormatter JSON_FORMAT = new JsonFormat();

    private static final ProtobufFormatter XML_FORMAT = new XmlFormat();

    private static final ProtobufFormatter HTML_FORMAT = new HtmlFormat();

    private List<Class> messages;

    @Autowired
    private JsonUtil jsonUtil;

    public CustomProtobufHttpMessageConverter(ListableBeanFactory listableBeanFactory) {
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
    protected void writeInternal(Message message, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        MediaType contentType = outputMessage.getHeaders().getContentType();
        if (contentType == null) {
            contentType = getDefaultContentType(message);
        }
        Charset charset = contentType.getCharset();
        if (charset == null) {
            charset = DEFAULT_CHARSET;
        }

        if (MediaType.TEXT_PLAIN.isCompatibleWith(contentType)) {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputMessage.getBody(), charset);
            TextFormat.print(message, outputStreamWriter);
            outputStreamWriter.flush();
        }
        else if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            String data = jsonUtil.toJson(message);
            FileCopyUtils.copy(data.getBytes(), outputMessage.getBody());
        }
        else if (MediaType.APPLICATION_XML.isCompatibleWith(contentType)) {
            XML_FORMAT.print(message, outputMessage.getBody(), charset);
        }
        else if (MediaType.TEXT_HTML.isCompatibleWith(contentType)) {
            HTML_FORMAT.print(message, outputMessage.getBody(), charset);
        }
        else if (PROTOBUF.isCompatibleWith(contentType)) {
            setJsonHeader(outputMessage);
            String data = jsonUtil.toJson(message);
            FileCopyUtils.copy(data.getBytes(), outputMessage.getBody());
        }
    }

    private void setJsonHeader(HttpOutputMessage outputMessage) {
        outputMessage.getHeaders().setContentType(MediaType.APPLICATION_JSON);
    }
}
