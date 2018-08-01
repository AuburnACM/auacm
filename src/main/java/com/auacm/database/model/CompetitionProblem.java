package com.auacm.database.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "comp_problems")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CompetitionProblem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;

    @ManyToOne
    @JoinColumn(name = "cid")
    private Competition competition;

    @ManyToOne
    @JoinColumn(name = "pid")
    private Problem problem;

    public CompetitionProblem(String label, Competition competition, Problem problem) {
        this.problem = problem;
        this.label = label;
        this.competition = competition;
    }
}
