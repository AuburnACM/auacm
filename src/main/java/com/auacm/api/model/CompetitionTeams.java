package com.auacm.api.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompetitionTeams {
    private Map<String, List<SimpleTeam>> teams;


    public CompetitionTeams() {
        this.teams = new HashMap<>();
    }

    public Map<String, List<SimpleTeam>> getTeams() {
        return teams;
    }

    public void setTeams(Map<String, List<SimpleTeam>> teams) {
        this.teams = teams;
    }
}
