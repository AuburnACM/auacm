package io.github.auburnacm.auacm.model;

import javax.persistence.*;

@Entity
@Table(name = "sample_cases")
public class SampleCase {
    @Id
    private long pid;

    @Column(name = "name_num")
    private int caseNumber;

    private String input;

    private String output;

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
}
