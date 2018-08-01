package com.auacm.api.model.response;

import com.auacm.api.model.RecentBlogPost;
import com.auacm.api.model.RecentCompetition;
import com.auacm.api.model.RecentSubmission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class ProfileResponse {
    private String displayName;
    private int problemsSolved;
    private List<RecentSubmission> recentAttempts;
    private List<RecentBlogPost> recentBlogPosts;
    private List<RecentCompetition> recentCompetitions;

    public ProfileResponse() {
        this.displayName = "";
        this.problemsSolved = 0;
        this.recentAttempts = new ArrayList<>();
        this.recentBlogPosts = new ArrayList<>();
        this.recentCompetitions = new ArrayList<>();
    }

    public ProfileResponse(String displayName) {
        this.displayName = displayName;
        this.problemsSolved = 0;
        this.recentAttempts = new ArrayList<>();
        this.recentBlogPosts = new ArrayList<>();
        this.recentCompetitions = new ArrayList<>();
    }

    public ProfileResponse(String displayName, int problemsSolved) {
        this.displayName = displayName;
        this.problemsSolved = problemsSolved;
        this.recentAttempts = new ArrayList<>();
        this.recentBlogPosts = new ArrayList<>();
        this.recentCompetitions = new ArrayList<>();
    }
}
