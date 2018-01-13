package com.auacm.converter;

import com.auacm.api.model.RecentBlogPost;
import com.auacm.api.model.RecentBlogPostList;
import com.auacm.api.proto.ProfileOuterClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RecentBlogPostListConverter extends AbstractHttpMessageConverter<RecentBlogPostList> {

    @Autowired
    private CustomProtobufHttpMessageConverter protobufConverter;

    public RecentBlogPostListConverter() {
        super(new MediaType("application", "json"));
    }

    @Override
    protected boolean supports(Class<?> aClass) {
        return RecentBlogPostList.class.isAssignableFrom(aClass);
    }

    @Override
    protected RecentBlogPostList readInternal(Class<? extends RecentBlogPostList> aClass, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    protected void writeInternal(RecentBlogPostList recentBlogPosts, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        ProfileOuterClass.RecentBlogPostResponse.Builder builder = ProfileOuterClass.RecentBlogPostResponse.newBuilder();
        for (RecentBlogPost blogPost : recentBlogPosts) {
            builder.addData(ProfileOuterClass.RecentBlogPost.newBuilder()
                    .setTitle(blogPost.getTitle()).setSubtitle(blogPost.getSubtitle())
                    .setPostTime(blogPost.getPostTime()).setId(blogPost.getId()));
        }
        protobufConverter.writeInternal(builder.build(), httpOutputMessage);
    }
}
