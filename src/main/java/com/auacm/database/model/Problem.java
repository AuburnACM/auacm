package com.auacm.database.model;

import com.auacm.api.model.request.CreateProblemRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Proxy
@Table(name = "problems")
@Getter
@Setter
@NoArgsConstructor
public class Problem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;

    @Column(name = "shortname")
    private String shortName;

    private String name;

    private String appeared;

    private String difficulty;

    private Long added;

    @Column(name = "comp_release")
    private Long competitionId;

    @OneToMany(mappedBy = "problem", fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<SampleCase> sampleCases;

    @JsonIgnore
    @OneToOne(mappedBy = "problem", fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinColumn(name = "pid")
    private ProblemData problemData;

    @OneToMany(mappedBy = "problem", fetch = FetchType.LAZY)
    private List<CompetitionProblem> competitionProblems;

    public Problem(CreateProblemRequest problemRequest) {
        this.name = problemRequest.getName();
        this.added = System.currentTimeMillis() / 1000;
        if (problemRequest.getDifficulty() != null) {
            this.difficulty = problemRequest.getDifficulty();
        } else {
            this.difficulty = "0";
        }
        this.problemData = new ProblemData();
        this.problemData.setDescription(problemRequest.getDescription());
        this.problemData.setInputDescription(problemRequest.getInputDescription());
        this.problemData.setOutputDescription(problemRequest.getOutputDescription());
        if (problemRequest.getTimeLimit() != null) {
            this.problemData.setTimeLimit(problemRequest.getTimeLimit());
        } else {
            // TODO Run problem to estimate time limit
            this.problemData.setTimeLimit(2L);
        }
        this.problemData.setProblem(this);
        sampleCases = new ArrayList<>();
        problemRequest.getSampleCaseList().forEach(sampleCase -> {
            SampleCase createSampleCase = new SampleCase(sampleCase);
            createSampleCase.setProblem(this);
            sampleCases.add(createSampleCase);
        });
    }

    public Problem(JsonObject object) {
        this.name = object.get("name").getAsString();
        this.added = System.currentTimeMillis() / 1000;
        this.difficulty = object.get("difficulty").getAsString();
        this.problemData = new ProblemData();
        this.problemData.setDescription(object.get("description").getAsString());
        this.problemData.setInputDescription(object.get("inputDescription").getAsString());
        this.problemData.setOutputDescription(object.get("outputDescription").getAsString());
        this.problemData.setTimeLimit(object.get("timeLimit").getAsLong());
        this.problemData.setProblem(this);
        this.sampleCases = new ArrayList<>();
        object.getAsJsonArray("sampleCases").forEach(sampleCase -> {
            SampleCase createSampleCase = new SampleCase(sampleCase.getAsJsonObject());
            createSampleCase.setProblem(this);
            sampleCases.add(createSampleCase);
        });
    }

    public Problem(Long pid) {
        this.pid = pid;
    }

    public void update(CreateProblemRequest problemRequest) {
        if (problemRequest.getName() != null) {
            this.name = problemRequest.getName();
        }
        if (problemRequest.getDifficulty() != null) {
            this.difficulty = problemRequest.getDifficulty();
        } else {
            this.difficulty = "0";
        }
        if (problemRequest.getDescription() != null) {
            this.problemData.setDescription(problemRequest.getDescription());
        }
        if (problemRequest.getInputDescription() != null) {
            this.problemData.setInputDescription(problemRequest.getInputDescription());
        }
        if (problemRequest.getOutputDescription() != null) {
            this.problemData.setOutputDescription(problemRequest.getOutputDescription());
        }
        if (problemRequest.getTimeLimit() != null) {
            this.problemData.setTimeLimit(problemRequest.getTimeLimit());
        }
        if (problemRequest.getSampleCases() != null) {
            List<SampleCase> toDelete = new ArrayList<>(sampleCases);
            problemRequest.getSampleCaseList().forEach(newSampleCase -> {
                boolean found = false;
                for (SampleCase oldSampleCase : sampleCases) {
                    if (oldSampleCase.getSampleCasePK().getCaseNum().equals(newSampleCase.getCaseNum())) {
                        toDelete.remove(oldSampleCase);                      // Remove the one we updated from the list to delete
                        oldSampleCase.setInput(newSampleCase.getInput());
                        oldSampleCase.setOutput(newSampleCase.getOutput());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    sampleCases.add(new SampleCase(newSampleCase));
                }
            });
            toDelete.forEach(sampleCase -> sampleCases.remove(toDelete));
        }
    }
}
