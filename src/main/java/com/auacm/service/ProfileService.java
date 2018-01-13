package com.auacm.service;

import com.auacm.api.model.*;

import java.util.List;

public interface ProfileService {
    Profile getProfile(String username);

    RecentBlogPostList getRecentBlogPosts(String username, int amount);

    RecentCompetitionList getRecentCompetitions(String username, int amount);

    RecentSubmissionList getRecentSubmissions(String username, int amount);
}
