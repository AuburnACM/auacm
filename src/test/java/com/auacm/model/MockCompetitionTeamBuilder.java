package com.auacm.model;

import com.auacm.api.model.CompetitionTeams;
import com.auacm.api.model.SimpleTeam;

import java.util.*;

public class MockCompetitionTeamBuilder {
    private HashMap<String, List<SimpleTeam>> teams;

    public MockCompetitionTeamBuilder() {
        teams = new HashMap<>();
    }

    public MockCompetitionTeamBuilder addUser(String username, String display, String team) {
        removeUser(username);
        if (!teams.containsKey(team)) {
            ArrayList<SimpleTeam> temp = new ArrayList<>();
            temp.add(new SimpleTeam(display, username));
            teams.put(team, temp);
        } else {
            SimpleTeam simpleTeam = new SimpleTeam(display, username);
            if (!teams.get(team).contains(simpleTeam)) {
                teams.get(team).add(simpleTeam);
            }
        }
        return this;
    }

    public MockCompetitionTeamBuilder removeUser(String username) {
        String removeTeam = null;
        for (Map.Entry<String, List<SimpleTeam>> teams : teams.entrySet()) {
            boolean found = false;
            for (SimpleTeam team : new ArrayList<>(teams.getValue())) {
                if (team.getUsername().equals(username)) {
                    teams.getValue().remove(team);
                    if (teams.getValue().size() == 0) {
                        removeTeam = teams.getKey();
                    }
                    found = true;
                    break;
                }
            }
            if (found) {
                break;
            }
        }
        if (removeTeam != null) {
            teams.remove(removeTeam);
        }
        return this;
    }

    public CompetitionTeams build() {
        CompetitionTeams teams = new CompetitionTeams();
        teams.setTeams(this.teams);
        return teams;
    }
}
