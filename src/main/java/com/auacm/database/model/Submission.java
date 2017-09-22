package com.auacm.database.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "submits")
public class Submission implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long job;

    private int pid;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    private User user;

    @Column(name = "shortname")
    private String shortName;

    @Column(name = "submit_time")
    private int submitTime;

    @Column(name = "auto_id")
    private boolean autoId;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "result")
    private String result;

    public long getJob() {
        return job;
    }

    public Submission setJob(long job) {
        this.job = job;
        return this;
    }

    public int getPid() {
        return pid;
    }

    public Submission setPid(int pid) {
        this.pid = pid;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Submission setUser(User user) {
        this.user = user;
        return this;
    }

    public String getShortName() {
        return shortName;
    }

    public Submission setShortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

    public int getSubmitTime() {
        return submitTime;
    }

    public Submission setSubmitTime(int submitTime) {
        this.submitTime = submitTime;
        return this;
    }

    public boolean isAutoId() {
        return autoId;
    }

    public Submission setAutoId(boolean autoId) {
        this.autoId = autoId;
        return this;
    }

    public String getFileType() {
        return fileType;
    }

    public Submission setFileType(String fileType) {
        this.fileType = fileType;
        return this;
    }

    public String getResult() {
        return result;
    }

    public Submission setResult(String result) {
        this.result = result;
        return this;
    }
}
