package com.auacm.database.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Scoreboard {
    private HashMap<String, ScoreboardTeam> teams;

    public Scoreboard() {
        this.teams = new HashMap<>();
    }

    public void addTeam(ScoreboardTeam team) {
        this.teams.put(team.getName(), team);
    }

    public List<ScoreboardTeam> getTeams() {
        return new ArrayList<>(teams.values());
    }
}
