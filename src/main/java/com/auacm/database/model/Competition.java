package com.auacm.database.model;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comp_names")
@Proxy(lazy = false)
public class Competition implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cid")
    private long cid;

    private String name;

    private Long start;

    private Long stop;

    private Boolean closed;

    @OneToMany(targetEntity = CompetitionProblem.class)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "cid", nullable = false, insertable = false)
    private List<CompetitionProblem> competitionProblems;

    @OneToMany(targetEntity = CompetitionUser.class)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "cid", nullable = false, insertable = false)
    private List<CompetitionUser> competitionUsers;

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getStop() {
        return stop;
    }

    public void setStop(Long stop) {
        this.stop = stop;
    }

    public Boolean isClosed() {
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }

    public List<CompetitionProblem> getCompetitionProblems() {
        if (competitionProblems == null) {
            return new ArrayList<>();
        } else {
            return competitionProblems;
        }
    }

    public void setCompetitionProblems(List<CompetitionProblem> competitionProblems) {
        this.competitionProblems = competitionProblems;
    }

    public List<CompetitionUser> getCompetitionUsers() {
        if (competitionUsers == null) {
            return new ArrayList<>();
        } else {
            return competitionUsers;
        }
    }

    public void setCompetitionUsers(List<CompetitionUser> competitionUsers) {
        this.competitionUsers = competitionUsers;
    }
}
