package com.auacm.api.model.response;

import com.auacm.api.model.Competition;
import com.auacm.api.model.CompetitionProblem;
import com.auacm.api.model.CompetitionTeam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CompetitionResponse {
    private Competition competition;
    private Map<String, CompetitionProblem> problems;
    private Map<String, CompetitionTeam> teams;

    public CompetitionResponse(com.auacm.database.model.Competition competition) {
        this.competition = new Competition(competition);
        this.problems = competition.getProblems();
        teams = competition.getTeams();
    }
}
