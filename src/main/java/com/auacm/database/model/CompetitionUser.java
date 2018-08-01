package com.auacm.database.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "comp_users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CompetitionUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String team;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "username")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cid")
    private Competition competition;

    public CompetitionUser(String username, String team, Competition competition) {
        this.user = new User(username);
        this.team = team;
        this.competition = competition;
    }
}
