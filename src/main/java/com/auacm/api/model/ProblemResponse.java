package com.auacm.api.model;

import com.auacm.database.model.Problem;
import com.auacm.database.model.ProblemData;
import com.auacm.database.model.SampleCase;
import java.util.List;

public class ProblemResponse {
    private long pid;

    private String shortname;

    private String name;

    private String appeared;

    private String difficulty;

    private long added;

    private int competitionId;

    private List<SampleCase> sampleCases;

    private ProblemData problemData;

    public ProblemResponse() {}

    public ProblemResponse(Problem problem) {
        this.pid = problem.getPid();
        this.shortname = problem.getShortName();
        this.name = problem.getName();
        this.appeared = problem.getAppeared();
        this.difficulty = problem.getDifficulty();
        this.added = problem.getAdded();
        this.competitionId = problem.getCompetitionId();
        this.sampleCases = problem.getSampleCases();
        this.problemData = problem.getProblemData();
    }

}
