package com.auacm.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "sample_cases")
public class SampleCase implements Serializable {

    @EmbeddedId
    private SampleCasePK sampleCasePK;

    private String input;

    private String output;

    @JsonIgnore
    @ManyToOne(targetEntity = Problem.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "pid", insertable=false, updatable=false)
    private Problem problem;

    public SampleCase() {
        this.sampleCasePK = new SampleCasePK();
    }


    public SampleCasePK getSampleCasePK() {
        return sampleCasePK;
    }

    public void setSampleCasePK(SampleCasePK sampleCasePK) {
        this.sampleCasePK = sampleCasePK;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }
}
