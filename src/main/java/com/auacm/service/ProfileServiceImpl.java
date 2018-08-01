package com.auacm.service;

import com.auacm.api.model.*;
import com.auacm.api.model.response.ProfileResponse;
import com.auacm.database.model.*;
import com.auacm.database.model.Competition;
import com.auacm.database.model.Submission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProfileServiceImpl implements ProfileService {
    private final int MAX_RECENT_ATTEMPTS = 5;
    private final int MAX_RECENT_COMPETITIONS = 5;
    private final int MAX_RECENT_BLOG_POSTS = 3;

    @Autowired
    private UserService userService;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private BlogPostService blogPostService;

    @Autowired
    private ProblemService problemService;

    @Override
    public ProfileResponse getProfile(String username) {
        User user = userService.getUser(username);
        if (user == null) {
            throw new UsernameNotFoundException("That user does not exist.");
        }
        ProfileResponse profile = new ProfileResponse(user.getDisplayName());
        profile.setProblemsSolved(submissionService.getTotalCorrectSubmissions(username));
        profile.setRecentAttempts(getRecentSubmissions(username, MAX_RECENT_ATTEMPTS));
        profile.setRecentBlogPosts(getRecentBlogPosts(username, MAX_RECENT_BLOG_POSTS));
        profile.setRecentCompetitions(getRecentCompetitions(username, MAX_RECENT_COMPETITIONS));
        return profile;
    }

    @Override
    public RecentBlogPostList getRecentBlogPosts(String username, int amount) {
        if (amount <= 0) {
            amount = MAX_RECENT_BLOG_POSTS;
        }
        User user = userService.getUser(username);
        List<BlogPost> blogPosts = blogPostService.getRecentBlogPostsForUser(username, Math.min(amount, MAX_RECENT_BLOG_POSTS));
        RecentBlogPostList blogPostArrayList = new RecentBlogPostList();
        for (BlogPost blogPost : blogPosts) {
            blogPostArrayList.add(new RecentBlogPost(blogPost));
        }
        return blogPostArrayList;
    }

    @Override
    public RecentCompetitionList getRecentCompetitions(String username, int amount) {
        if (amount <= 0) {
            amount = MAX_RECENT_COMPETITIONS;
        }
        User user = userService.getUser(username);
        List<CompetitionUser> compUsers = competitionService.getRecentCompetitionsForUser(username, Math.min(amount, MAX_RECENT_COMPETITIONS));
        RecentCompetitionList competitions = new RecentCompetitionList();
        for (CompetitionUser competitionUser : compUsers) {
            Competition competition = competitionService.getCompetitionById(competitionUser.getCompetition().getCid());
            RecentCompetition recentCompetition = new RecentCompetition();
            recentCompetition.setCid(competitionUser.getCompetition().getCid());
            recentCompetition.setCompName(competition.getName());
            recentCompetition.setTeamName(competitionUser.getTeam());
            recentCompetition.setTeamSize(competition.getUserCountForTeam(competitionUser.getTeam()));
            competitions.add(recentCompetition);
        }
        return competitions;
    }

    @Override
    public RecentSubmissionList getRecentSubmissions(String username, int amount) {
        if (amount <= 0) {
            amount = MAX_RECENT_ATTEMPTS;
        }
        User user = userService.getUser(username);
        List<Submission> submissions = submissionService.getRecentSubmissions(username, Math.min(amount, MAX_RECENT_ATTEMPTS));
        Map<Long, RecentSubmission> map = new LinkedHashMap<>();
        for (Submission submission : submissions) {
            Problem problem = problemService.getProblemForPid(submission.getPid());
            if (map.containsKey(problem.getPid())) {
                map.get(problem.getPid()).incrementSubmissionCount();
                map.get(problem.getPid()).addSubmissionId(submission.getJob());
                if (!map.get(problem.getPid()).isCorrect()) {
                    map.get(problem.getPid()).setCorrect(submission.getResult().equals("good"));
                }
            } else {
                RecentSubmission recentSubmission = new RecentSubmission(problem);
                recentSubmission.setSubmissionCount(1);
                recentSubmission.addSubmissionId(submission.getJob());
                recentSubmission.setCorrect(submission.getResult().equals("good"));
                map.put(problem.getPid(), recentSubmission);
            }
        }
        return new RecentSubmissionList(map.values());
    }
}
