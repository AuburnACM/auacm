package com.auacm.converter;

import com.auacm.api.model.RecentCompetition;
import com.auacm.api.model.RecentCompetitionList;
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
public class RecentCompetitionListConverter extends AbstractHttpMessageConverter<RecentCompetitionList> {

    @Autowired
    private CustomProtobufHttpMessageConverter protobufConverter;

    public RecentCompetitionListConverter() {
        super(new MediaType("application", "json"));
    }

    @Override
    protected boolean supports(Class<?> aClass) {
        return RecentCompetitionList.class.isAssignableFrom(aClass);
    }

    @Override
    protected RecentCompetitionList readInternal(Class<? extends RecentCompetitionList> aClass, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    protected void writeInternal(RecentCompetitionList recentCompetitions, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        ProfileOuterClass.RecentCompetitionResponse.Builder builder = ProfileOuterClass.RecentCompetitionResponse.newBuilder();
        for (RecentCompetition recentCompetition: recentCompetitions) {
            builder.addData(ProfileOuterClass.RecentCompetition.newBuilder()
                    .setTeamSize(recentCompetition.getTeamSize()).setTeamName(recentCompetition.getTeamName())
                    .setCid(recentCompetition.getCid()).setCompName(recentCompetition.getCompName()));
        }
        protobufConverter.writeInternal(builder.build(), httpOutputMessage);
    }
}
