package com.auacm.service;

import com.auacm.database.model.Submission;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubmissionService {
    List<Submission> getAllSubmissionsBetween(long start, long finish);

    List<Submission> getAllSubmissionsForUsernameBetween(String username, long start, long finish);

    List<Submission> getRecentSubmissions(String username, int amount);

    int getTotalCorrectSubmissions(String username);
}
