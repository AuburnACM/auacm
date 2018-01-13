package com.auacm.converter;

import com.auacm.api.model.RecentCompetitionList;
import com.auacm.api.model.RecentSubmission;
import com.auacm.api.model.RecentSubmissionList;
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
public class RecentSubmissionListConverter extends AbstractHttpMessageConverter<RecentSubmissionList> {

    @Autowired
    private CustomProtobufHttpMessageConverter protobufConverter;

    public RecentSubmissionListConverter() {
        super(new MediaType("application", "json"));
    }

    @Override
    protected boolean supports(Class<?> aClass) {
        return RecentSubmissionList.class.isAssignableFrom(aClass);
    }

    @Override
    protected RecentSubmissionList readInternal(Class<? extends RecentSubmissionList> aClass, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    protected void writeInternal(RecentSubmissionList recentSubmissions, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        ProfileOuterClass.RecentSubmissionResponse.Builder builder = ProfileOuterClass.RecentSubmissionResponse.newBuilder();
        for (RecentSubmission recentSubmission: recentSubmissions) {
            builder.addData(ProfileOuterClass.RecentSubmission.newBuilder()
                    .setSubmissionCount(recentSubmission.getSubmissionCount()).setCorrect(recentSubmission.isCorrect())
                    .setShortName(recentSubmission.getShortName()).setPid(recentSubmission.getPid())
                    .setName(recentSubmission.getName()).addAllSubmissionIds(recentSubmission.getSubmissionIds()));
        }
        protobufConverter.writeInternal(builder.build(), httpOutputMessage);
    }
}
