package io.github.auburnacm.auacm.database.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "problem_solved")
public class SolvedProblem implements Serializable {
    @Id
    private long pid;

    private String username;

    @Column(name = "submit_time")
    private long submitTime;

    public SolvedProblem() {}

    public SolvedProblem(long pid, String username) {
        this.pid = pid;
        this.username = username;
        this.submitTime = System.currentTimeMillis() / 1000;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(long submitTime) {
        this.submitTime = submitTime;
    }
}
