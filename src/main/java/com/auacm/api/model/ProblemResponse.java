package com.auacm.api.model;

import com.auacm.database.model.Problem;

public class ProblemResponse {

    private long pid;

    private String shortname;

    private String name;

    private String appeared;

    private String difficulty;

    private String url;

    private long added;

    private Integer compRelease;

    public ProblemResponse() {
    }

    public ProblemResponse(Problem problem) {
        this.pid = problem.getPid();
        this.shortname = problem.getShortName();
        this.name = problem.getName();
        this.appeared = problem.getAppeared();
        this.difficulty = problem.getDifficulty();
        this.added = problem.getAdded();
        this.compRelease = problem.getCompetitionId() == null ? 0 : problem.getCompetitionId();
        this.url = problem.getUrl();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppeared() {
        return appeared;
    }

    public void setAppeared(String appeared) {
        this.appeared = appeared;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public long getAdded() {
        return added;
    }

    public void setAdded(long added) {
        this.added = added;
    }

    public Integer getCompRelease() {
        return compRelease;
    }

    public void setCompRelease(Integer compRelease) {
        this.compRelease = compRelease;
    }

}
