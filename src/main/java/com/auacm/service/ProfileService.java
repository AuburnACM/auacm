package com.auacm.service;

import com.auacm.api.model.response.ProfileResponse;
import com.auacm.api.model.RecentBlogPostList;
import com.auacm.api.model.RecentCompetitionList;
import com.auacm.api.model.RecentSubmissionList;

public interface ProfileService {
    ProfileResponse getProfile(String username);

    RecentBlogPostList getRecentBlogPosts(String username, int amount);

    RecentCompetitionList getRecentCompetitions(String username, int amount);

    RecentSubmissionList getRecentSubmissions(String username, int amount);
}
