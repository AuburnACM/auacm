package com.auacm.converter;

import com.auacm.api.model.Profile;
import com.auacm.api.model.RecentBlogPost;
import com.auacm.api.model.RecentCompetition;
import com.auacm.api.model.RecentSubmission;
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
public class ProfileConverter extends AbstractHttpMessageConverter<Profile> {
    @Autowired
    private CustomProtobufHttpMessageConverter protobufConverter;

    public ProfileConverter() {
        super(new MediaType("application", "json"));
    }

    @Override
    protected boolean supports(Class<?> aClass) {
        return Profile.class.isAssignableFrom(aClass);
    }

    @Override
    protected Profile readInternal(Class<? extends Profile> aClass, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        return new Profile();
    }

    @Override
    protected void writeInternal(Profile profile, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        ProfileOuterClass.Profile.Builder builder = ProfileOuterClass.Profile.newBuilder();
        builder.setDisplayName(profile.getDisplayName()).setProblemsSolved(profile.getProblemsSolved());
        for (RecentSubmission recentSubmission : profile.getRecentAttempts()) {
            builder.addRecentAttempts(ProfileOuterClass.RecentSubmission.newBuilder()
                    .setCorrect(recentSubmission.isCorrect()).setName(recentSubmission.getName())
                    .setShortName(recentSubmission.getShortName()).setPid(recentSubmission.getPid())
                    .setSubmissionCount(recentSubmission.getSubmissionCount())
                    .addAllSubmissionIds(recentSubmission.getSubmissionIds()));
        }
        for (RecentCompetition recentCompetition : profile.getRecentCompetitions()) {
            builder.addRecentCompetitions(ProfileOuterClass.RecentCompetition.newBuilder()
                    .setCid(recentCompetition.getCid()).setCompName(recentCompetition.getCompName())
                    .setTeamName(recentCompetition.getTeamName()).setTeamSize(recentCompetition.getTeamSize()));
        }
        for (RecentBlogPost recentBlogPost : profile.getRecentBlogPosts()) {
            builder.addRecentBlogPosts(ProfileOuterClass.RecentBlogPost.newBuilder()
                    .setId(recentBlogPost.getId()).setPostTime(recentBlogPost.getPostTime())
                    .setSubtitle(recentBlogPost.getSubtitle()).setTitle(recentBlogPost.getTitle()));
        }
        protobufConverter.writeInternal(ProfileOuterClass.ProfileResponse.newBuilder().setData(builder).build(), httpOutputMessage);
    }
}
