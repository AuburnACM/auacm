package com.auacm.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "problem_data")
public class ProblemData implements Serializable {

    @JsonIgnore
    @Id
    private long pid;

    @Column(name = "time_limit")
    private Long timeLimit;

    private String description;

    @Column(name = "input_desc")
    private String inputDescription;

    @Column(name = "output_desc")
    private String outputDescription;

    @JsonIgnore
    @OneToOne(targetEntity = Problem.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "pid", nullable = false)
    private Problem problem;

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public long getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInputDescription() {
        return inputDescription;
    }

    public void setInputDescription(String inputDescription) {
        this.inputDescription = inputDescription;
    }

    public String getOutputDescription() {
        return outputDescription;
    }

    public void setOutputDescription(String outputDescription) {
        this.outputDescription = outputDescription;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }
}
