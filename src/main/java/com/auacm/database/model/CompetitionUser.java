package com.auacm.database.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "comp_users")
public class CompetitionUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long cid;

    private String username;

    private String team;

//    @ManyToOne(targetEntity = Competition.class, fetch = FetchType.LAZY)
//    @JoinColumn(name = "cid", nullable = false, insertable = false)
//    private Competition competition;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

//    public Competition getCompetition() {
//        return competition;
//    }
//
//    public void setCompetition(Competition competition) {
//        this.competition = competition;
//    }
}