package com.auacm.service;

import com.auacm.database.dao.SubmissionDao;
import com.auacm.database.model.Submission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubmissionServiceImpl implements SubmissionService {
    @Autowired
    private SubmissionDao submissionDao;

    @Override
    public List<Submission> getAllSubmissionsBetween(long start, long finish) {
        return submissionDao.findAllBySubmitTimeGreaterThanEqualAndSubmitTimeLessThanOrderBySubmitTime(start, finish);
    }

    @Override
    public List<Submission> getAllSubmissionsForUsernameBetween(String username, long start, long finish) {
        return submissionDao.findAllByUsernameAndSubmitTimeGreaterThanEqualAndSubmitTimeLessThanOrderBySubmitTime(username, start, finish);
    }

    @Override
    public List<Submission> getRecentSubmissions(String username, int amount) {
        List<Submission> submissions = submissionDao.findAllByUsernameOrderByJob(username, new PageRequest(0, 100));
        List<Submission> finalSubmission = new ArrayList<>();
        int count = 0;
        for (Submission submission : submissions) {
            if (count == 0) {
                finalSubmission.add(submission);
                count++;
            } else {
                if (finalSubmission.get(finalSubmission.size() - 1).getPid() == submission.getPid()) {
                    finalSubmission.add(submission);
                } else {
                    count++;
                    if (count > amount) {
                        break;
                    } else {
                        finalSubmission.add(submission);
                    }
                }
            }
        }
        return finalSubmission;
    }

    @Override
    public int getTotalCorrectSubmissions(String username) {
        return submissionDao.countDistinctByUsernameAndResult(username, "good");
    }
}
