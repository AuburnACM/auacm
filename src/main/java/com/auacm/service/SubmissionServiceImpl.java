package com.auacm.service;

import com.auacm.api.model.JudgeSubmission;
import com.auacm.api.model.SubmissionList;
import com.auacm.api.model.SubmissionStatus;
import com.auacm.database.dao.SubmissionDao;
import com.auacm.database.model.Problem;
import com.auacm.database.model.Submission;
import com.auacm.database.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubmissionServiceImpl implements SubmissionService {
    private final String SUBMISSION_TOPIC = "judge-submission";

    @Autowired
    private SubmissionDao submissionDao;

    @Autowired
    private FileSystemService fileSystemService;

    @Autowired
    private ProblemService problemService;

    @Autowired
    private KafkaTemplate<String, JudgeSubmission> kafkaTemplate;

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
        List<Submission> submissions = submissionDao.findAllByUsernameOrderByJob(username, PageRequest.of(0, 100));
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
    public List<Object[]> getSubmissionsForCid(Long competitionId) {
        return submissionDao.findAllByCid(competitionId);
    }

    @Override
    public int getTotalCorrectSubmissions(String username) {
        return submissionDao.countDistinctByUsernameAndResult(username, "good");
    }

    @Override
    public SubmissionStatus submit(com.auacm.api.model.Submission submission) {
        Submission submission1 = new Submission();
        submission1.setSubmitTime(System.currentTimeMillis() / 1000);
        Problem problem = problemService.getProblemForPid(submission.getPid());
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        submission1.setShortName(problem.getShortName());
        submission1.setUsername(user.getUsername());
        submission1.setResult("start");
        String[] name = submission.getFile().getOriginalFilename().split("\\.");
        String extension = name[name.length - 1];
        submission1.setFileType(extension);
        submission1.setAutoId(false);
        submission1.setPid(submission.getPid());
        submission1 = submissionDao.save(submission1);
        fileSystemService.saveSubmissionFile(submission1.getJob() + "", submission.getFile());
        // Return submission status
        judge(submission1, submission.getFile().getOriginalFilename());
        SubmissionStatus status = new SubmissionStatus();
        status.setProblemId(submission.getPid());
        status.setStatus("start");
        status.setSubmissionId(submission1.getJob());
        status.setSubmitTime(submission1.getSubmitTime());
        status.setTestNum(0);
        status.setUsername(user.getUsername());
        return status;
    }

    @Override
    public SubmissionList getSubmissionsCurrentUser(int limit) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return getSubmissionsForUser(user.getUsername(), limit);
    }

    @Override
    public SubmissionList getSubmissionsForUser(String username, int limit) {
        List<Submission> submissions = submissionDao.findAllByUsernameOrderByJob(username, PageRequest.of(0, limit));
        SubmissionList list = new SubmissionList();
        for (Submission submission : submissions) {
            list.add(new SubmissionStatus(submission));
        }
        return list;
    }

    @Override
    public void judge(Submission submission, String submissionName) {
        JudgeSubmission judgeSubmission = new JudgeSubmission();
        judgeSubmission.setSubmissionFile(fileSystemService.getSubmissionFileAsByteArray(submission.getJob() + "", submissionName));
        judgeSubmission.setInputData(fileSystemService.getInputFilesAsByteArrays(submission.getPid() + ""));
        judgeSubmission.setOutputData(fileSystemService.getInputFilesAsByteArrays(submission.getPid() + ""));
        judgeSubmission.setSubmitTime(submission.getSubmitTime());
        judgeSubmission.setFileType(submission.getFileType());
        judgeSubmission.setProblemId(submission.getPid());
        judgeSubmission.setSubmissionId(submission.getJob());
        judgeSubmission.setUsername(submission.getUsername());
        kafkaTemplate.send(SUBMISSION_TOPIC, judgeSubmission);
    }
}
