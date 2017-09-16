package io.github.auburnacm.auacm.model;

import javax.persistence.*;
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
    private int competitionId;

    @OneToMany(targetEntity = SampleCase.class, fetch = FetchType.EAGER)
//    @JoinColumn(name = "pid", nullable = false, )
    private List<SampleCase> sampleCases;

    @OneToOne(targetEntity = ProblemData.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "pid", nullable = false)
    private List<ProblemData> problemData;

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

    public int getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(int competitionId) {
        this.competitionId = competitionId;
    }

    public List<SampleCase> getSampleCases() {
        return sampleCases;
    }

    public void setSampleCases(List<SampleCase> sampleCases) {
        this.sampleCases = sampleCases;
    }

    public List<ProblemData> getProblemData() {
        return problemData;
    }

    public void setProblemData(List<ProblemData> problemData) {
        this.problemData = problemData;
    }
}
