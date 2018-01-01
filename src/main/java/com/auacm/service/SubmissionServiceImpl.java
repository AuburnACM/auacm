package com.auacm.service;

import com.auacm.database.dao.SubmissionDao;
import com.auacm.database.model.Submission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public List<Submission> getAllSubmissionsForUserNameBetween(String username, long start, long finish) {
        return submissionDao.findAllByUserNameAndSubmitTimeGreaterThanEqualAndSubmitTimeLessThanOrderBySubmitTime(username, start, finish);
    }
}
