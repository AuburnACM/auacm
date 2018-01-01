package com.auacm.api.model;

import java.util.List;

public class CreateCompetition {
    private String name;

    private Long startTime;

    private Long length;

    private Boolean closed;

    private List<Long> problems;

    private List<String> userNames;

    public List<String> getUserNames() {
        return userNames;
    }

    public void setUserNames(List<String> userNames) {
        this.userNames = userNames;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public Boolean isClosed() {
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }

    public List<Long> getProblems() {
        return problems;
    }

    public void setProblems(List<Long> problems) {
        this.problems = problems;
    }
}
