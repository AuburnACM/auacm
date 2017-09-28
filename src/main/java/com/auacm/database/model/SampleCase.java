package com.auacm.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "sample_cases")
public class SampleCase implements Serializable {

    @JsonIgnore
    @Id
    private long pid;

    @Column(name = "case_num")
    private int case_num;

    private String input;

    private String output;

    @JsonIgnore
    @ManyToOne(targetEntity = Problem.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "pid", insertable=false, updatable=false)
    private Problem problem;

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public int getCase_num() {
        return case_num;
    }

    public void setCase_num(int case_num) {
        this.case_num = case_num;
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
