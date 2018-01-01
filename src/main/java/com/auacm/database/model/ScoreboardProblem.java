package com.auacm.database.model;

public class ScoreboardProblem {
    private CompetitionProblem competitionProblem;
    private Problem problem;
    private String status;
    private int submitCount;
    private long submitTime;

    public ScoreboardProblem() {
        status = "unattempted";
    }

    public ScoreboardProblem(CompetitionProblem competitionProblem, Problem problem) {
        this.competitionProblem = competitionProblem;
        this.problem = problem;
        status = "unattempted";
    }

    public CompetitionProblem getCompetitionProblem() {
        return competitionProblem;
    }

    public void setCompetitionProblem(CompetitionProblem competitionProblem) {
        this.competitionProblem = competitionProblem;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSubmitCount() {
        return submitCount;
    }

    public void incrementSubmitCount() {
        submitCount++;
    }

    public void setSubmitCount(int submitCount) {
        this.submitCount = submitCount;
    }

    public long getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(long submitTime) {
        this.submitTime = submitTime;
    }
}
