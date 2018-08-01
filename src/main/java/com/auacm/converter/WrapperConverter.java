package com.auacm.converter;

import com.auacm.api.model.RecentBlogPostList;
import com.auacm.api.model.RecentCompetitionList;
import com.auacm.api.model.RecentSubmissionList;
import com.auacm.api.model.ResponseWrapper;
import com.auacm.api.model.response.*;
import com.auacm.database.model.User;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class WrapperConverter extends AbstractHttpMessageConverter<Object> {
    private final MappingJackson2HttpMessageConverter converter;

    public WrapperConverter(MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter) {
        super(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON_UTF8);
        this.converter = mappingJackson2HttpMessageConverter;
    }

    @Override
    protected boolean supports(Class<?> aClass) {
        return BlogPostListResponse.class.isAssignableFrom(aClass)
                || BlogPostResponse.class.isAssignableFrom(aClass)
                || CompetitionResponse.class.isAssignableFrom(aClass)
                || CompetitionTeamResponse.class.isAssignableFrom(aClass)
                || CreateProblemResponse.class.isAssignableFrom(aClass)
                || MeResponse.class.isAssignableFrom(aClass)
                || ProfileResponse.class.isAssignableFrom(aClass)
                || ProblemListResponse.class.isAssignableFrom(aClass)
                || RankedUserResponse.class.isAssignableFrom(aClass)
                || RankedUserListResponse.class.isAssignableFrom(aClass)
                || RecentBlogPostList.class.isAssignableFrom(aClass)
                || RecentCompetitionList.class.isAssignableFrom(aClass)
                || RecentSubmissionList.class.isAssignableFrom(aClass)
                || User.class.isAssignableFrom(aClass);
    }

    @Override
    protected Object readInternal(Class<?> aClass, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    protected void writeInternal(Object o, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        if (httpOutputMessage instanceof ServletServerHttpResponse) {
            converter.write(new ResponseWrapper(o,((ServletServerHttpResponse) httpOutputMessage).getServletResponse().getStatus(),
                    HttpStatus.valueOf(((ServletServerHttpResponse) httpOutputMessage).getServletResponse().getStatus()).getReasonPhrase()),
                    MediaType.APPLICATION_JSON_UTF8, httpOutputMessage);
        }
    }
}
