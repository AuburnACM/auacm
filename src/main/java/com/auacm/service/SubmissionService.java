package com.auacm.service;

import com.auacm.database.model.Submission;

import java.util.List;

public interface SubmissionService {
    List<Submission> getAllSubmissionsBetween(long start, long finish);

    List<Submission> getAllSubmissionsForUserNameBetween(String username, long start, long finish);
}
