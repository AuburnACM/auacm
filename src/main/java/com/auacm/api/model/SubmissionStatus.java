package com.auacm.api.model;

import com.auacm.database.model.Submission;

public class SubmissionStatus {
    private long submissionId;
    private long problemId;
    private String username;
    private long submitTime;
    private long testNum;
    private String status;
    private String fileType;

    public SubmissionStatus() {}

    public SubmissionStatus(Submission submission) {
        this.submissionId = submission.getJob();
        this.problemId = submission.getPid();
        this.username = submission.getUsername();
        this.submitTime = submission.getSubmitTime();
        this.status = submission.getResult();
        this.fileType = submission.getFileType();
    }

    public long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(long submissionId) {
        this.submissionId = submissionId;
    }

    public long getProblemId() {
        return problemId;
    }

    public void setProblemId(long problemId) {
        this.problemId = problemId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(long submitTime) {
        this.submitTime = submitTime;
    }

    public long getTestNum() {
        return testNum;
    }

    public void setTestNum(long testNum) {
        this.testNum = testNum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
