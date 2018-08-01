package com.auacm.service;

import com.auacm.api.model.SubmissionList;
import com.auacm.api.model.SubmissionStatus;
import com.auacm.database.model.Submission;

import java.util.List;

public interface SubmissionService {
    List<Submission> getAllSubmissionsBetween(long start, long finish);

    List<Submission> getAllSubmissionsForUsernameBetween(String username, long start, long finish);

    List<Submission> getRecentSubmissions(String username, int amount);

    List<Object[]> getSubmissionsForCid(Long competitionId);

    int getTotalCorrectSubmissions(String username);

    SubmissionStatus submit(com.auacm.api.model.Submission submission);

    SubmissionList getSubmissionsCurrentUser(int limit);

    SubmissionList getSubmissionsForUser(String username, int limit);

    void judge(Submission submission, String submissionName);
}
