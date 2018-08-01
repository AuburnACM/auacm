package com.auacm.api.model.response;

import com.auacm.api.model.SampleCase;
import com.auacm.database.model.Problem;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class CreateProblemResponse {
    private Long added;
    private String appearedIn;
    private Long competitionId;
    private String description;
    private String difficulty;
    private String inputDescription;
    private String name;
    private String outputDescription;
    private Long pid;
    private List<SampleCase> sampleCases;
    private String shortName;
    private Long timeLimit;

    public CreateProblemResponse(Problem problem) {
        this.added = problem.getAdded();
        this.appearedIn = problem.getAppeared();
        this.competitionId = problem.getCompetitionId();
        this.description = problem.getProblemData().getDescription();
        this.difficulty = problem.getDifficulty();
        this.inputDescription = problem.getProblemData().getInputDescription();
        this.name = problem.getName();
        this.outputDescription = problem.getProblemData().getOutputDescription();
        this.pid = problem.getPid();
        this.sampleCases = new ArrayList<>();
        problem.getSampleCases().forEach(sampleCase -> {
            this.sampleCases.add(new SampleCase(sampleCase));
        });
        this.shortName = problem.getShortName();
        this.timeLimit = problem.getProblemData().getTimeLimit();
    }

    public String getUrl() {
        return "problems/" + shortName + "/info.pdf";
    }
}
