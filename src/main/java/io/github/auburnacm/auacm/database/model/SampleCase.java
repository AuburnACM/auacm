package io.github.auburnacm.auacm.database.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "sample_cases")
public class SampleCase implements Serializable {
    @Id
    private long pid;

    @Column(name = "name_num")
    private int caseNumber;

    private String input;

    private String output;

    @ManyToOne(targetEntity = Problem.class, fetch = FetchType.LAZY)
//    @JoinColumn(name = "pid")
    private Problem problem;

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public int getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(int caseNumber) {
        this.caseNumber = caseNumber;
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
