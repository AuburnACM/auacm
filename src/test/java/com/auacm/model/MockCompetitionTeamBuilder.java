package com.auacm.model;

import com.auacm.api.model.BasicUser;
import com.auacm.api.model.request.UpdateTeamsRequest;
import com.auacm.database.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockCompetitionTeamBuilder {
    private HashMap<String, List<BasicUser>> teams;

    public MockCompetitionTeamBuilder() {
        teams = new HashMap<>();
    }

    public MockCompetitionTeamBuilder addUser(String username, String display, String team) {
        removeUser(username);
        if (!teams.containsKey(team)) {
            ArrayList<BasicUser> temp = new ArrayList<>();
            temp.add(new BasicUser(display, username));
            teams.put(team, temp);
        } else {
            BasicUser simpleTeam = new BasicUser(display, username);
            if (!teams.get(team).contains(simpleTeam)) {
                teams.get(team).add(simpleTeam);
            }
        }
        return this;
    }

    public MockCompetitionTeamBuilder removeUser(String username) {
        String removeTeam = null;
        for (Map.Entry<String, List<BasicUser>> teams : teams.entrySet()) {
            boolean found = false;
            for (BasicUser team : new ArrayList<>(teams.getValue())) {
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

    public UpdateTeamsRequest build() {
        UpdateTeamsRequest teams = new UpdateTeamsRequest();
        teams.setTeams(this.teams);
        return teams;
    }
}
