package com.auacm.database.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "submits")
public class Submission implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long job;

    private long pid;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "username", updatable = false, insertable = false)
    private User user;

    @Column(name = "username")
    private String username;

    @Column(name = "shortname")
    private String shortName;

    @Column(name = "submit_time")
    private Long submitTime;

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

    public long getPid() {
        return pid;
    }

    public Submission setPid(long pid) {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    public String getShortName() {
        return shortName;
    }

    public Submission setShortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

    public Long getSubmitTime() {
        return submitTime;
    }

    public Submission setSubmitTime(Long submitTime) {
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
