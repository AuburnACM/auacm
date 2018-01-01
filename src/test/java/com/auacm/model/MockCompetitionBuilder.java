package com.auacm.model;

import com.auacm.api.model.CreateCompetition;

import java.util.ArrayList;

public class MockCompetitionBuilder {
    private CreateCompetition competition;

    public MockCompetitionBuilder() {
        competition = new CreateCompetition();
        competition = new CreateCompetition();
        competition.setName("Test Competition");
        competition.setClosed(false);
        competition.setLength(3600L);
        competition.setStartTime(100L);
        ArrayList<Long> problems = new ArrayList<>();
        problems.add(1L);
        competition.setProblems(problems);
        competition.setUserNames(new ArrayList<>());
    }

    public MockCompetitionBuilder setName(String name) {
        this.competition.setName(name);
        return this;
    }

    public MockCompetitionBuilder setClosed(boolean closed) {
        this.competition.setClosed(closed);
        return this;
    }

    public MockCompetitionBuilder setLength(long length) {
        this.competition.setLength(length);
        return this;
    }

    public MockCompetitionBuilder setStartTime(long startTime) {
        this.competition.setStartTime(startTime);
        return this;
    }

    public MockCompetitionBuilder addProblem(long problemId) {
        if (!this.competition.getProblems().contains(problemId)) {
            this.competition.getProblems().add(problemId);
        }
        return this;
    }

    public MockCompetitionBuilder addUser(String userName) {
        if (!this.competition.getUserNames().contains(userName)) {
            this.competition.getUserNames().add(userName);
        }
        return this;
    }

    public CreateCompetition build() {
        return competition;
    }
}
