package com.auacm.database.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "problems")
public class Problem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long pid;

    @Column(name = "shortname")
    private String shortName;

    private String name;

    private String appeared;

    private String difficulty;

    private long added;

    @Column(name = "comp_release")
    private Integer competitionId;

    @OneToMany(targetEntity = SampleCase.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "pid", nullable = false, insertable=false, updatable=false)
    private List<SampleCase> sampleCases;

    @OneToOne(targetEntity = ProblemData.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "pid", nullable = false)
    private ProblemData problemData;

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppeared() {
        return appeared;
    }

    public void setAppeared(String appeared) {
        this.appeared = appeared;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public long getAdded() {
        return added;
    }

    public void setAdded(long added) {
        this.added = added;
    }

    public Integer getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Integer competitionId) {
        this.competitionId = competitionId;
    }

    public List<SampleCase> getSampleCases() {
        return sampleCases;
    }

    public void setSampleCases(List<SampleCase> sampleCases) {
        this.sampleCases = sampleCases;
    }

    public ProblemData getProblemData() { return problemData; }

    public void setProblemData(ProblemData problemData) { this.problemData = problemData; }
}
