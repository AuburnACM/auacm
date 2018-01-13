package com.auacm.api.model;

import com.auacm.database.model.Problem;

import java.util.ArrayList;
import java.util.List;

public class RecentSubmission {
    private boolean correct;
    private String name;
    private String shortName;
    private long pid;
    private int submissionCount;
    private List<Long> submissionIds;

    public RecentSubmission() {
        this.submissionIds = new ArrayList<>();
    }

    public RecentSubmission(Problem problem) {
        this.name = problem.getName();
        this.shortName = problem.getShortName();
        this.pid = problem.getPid();
        this.submissionIds = new ArrayList<>();
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public int getSubmissionCount() {
        return submissionCount;
    }

    public void setSubmissionCount(int submissionCount) {
        this.submissionCount = submissionCount;
    }

    public void incrementSubmissionCount() {
        submissionCount++;
    }

    public List<Long> getSubmissionIds() {
        return submissionIds;
    }

    public void setSubmissionIds(List<Long> submissionIds) {
        this.submissionIds = submissionIds;
    }

    public void addSubmissionId(long id) {
        this.submissionIds.add(id);
    }
}
