package io.github.auburnacm.auacm.model;

import javax.persistence.*;

@Entity
@Table(name = "problem_data")
public class ProblemData {
    @Id
    private long pid;

    @Column(name = "time_limit")
    private int timeLimit;

    private String description;

    @Column(name = "input_desc")
    private String inputDescription;

    @Column(name = "output_desc")
    private String outputDescription;

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
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
}
