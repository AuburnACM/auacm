package com.auacm.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CompetitionProblem {
    private String name;
    private Long pid;
    private String shortName;

    public CompetitionProblem(com.auacm.database.model.CompetitionProblem competitionProblem) {
        this.name = competitionProblem.getProblem().getName();
        this.pid = competitionProblem.getProblem().getPid();
        this.shortName = competitionProblem.getProblem().getShortName();
    }
}
