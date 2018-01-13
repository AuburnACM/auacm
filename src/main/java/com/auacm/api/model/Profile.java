package com.auacm.api.model;

import java.util.ArrayList;
import java.util.List;

public class Profile {
    private String displayName;
    private int problemsSolved;
    private List<RecentSubmission> recentAttempts;
    private List<RecentBlogPost> recentBlogPosts;
    private List<RecentCompetition> recentCompetitions;

    public Profile() {
        this.displayName = "";
        this.problemsSolved = 0;
        this.recentAttempts = new ArrayList<>();
        this.recentBlogPosts = new ArrayList<>();
        this.recentCompetitions = new ArrayList<>();
    }

    public Profile(String displayName) {
        this.displayName = displayName;
        this.problemsSolved = 0;
        this.recentAttempts = new ArrayList<>();
        this.recentBlogPosts = new ArrayList<>();
        this.recentCompetitions = new ArrayList<>();
    }

    public Profile(String displayName, int problemsSolved) {
        this.displayName = displayName;
        this.problemsSolved = problemsSolved;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getProblemsSolved() {
        return problemsSolved;
    }

    public void setProblemsSolved(int problemsSolved) {
        this.problemsSolved = problemsSolved;
    }

    public List<RecentSubmission> getRecentAttempts() {
        return recentAttempts;
    }

    public void setRecentAttempts(List<RecentSubmission> recentAttempts) {
        this.recentAttempts = recentAttempts;
    }

    public List<RecentBlogPost> getRecentBlogPosts() {
        return recentBlogPosts;
    }

    public void setRecentBlogPosts(List<RecentBlogPost> recentBlogPosts) {
        this.recentBlogPosts = recentBlogPosts;
    }

    public List<RecentCompetition> getRecentCompetitions() {
        return recentCompetitions;
    }

    public void setRecentCompetitions(List<RecentCompetition> recentCompetitions) {
        this.recentCompetitions = recentCompetitions;
    }
}
