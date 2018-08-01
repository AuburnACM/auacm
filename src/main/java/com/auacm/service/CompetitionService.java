package com.auacm.service;

import com.auacm.api.model.request.CreateCompetitionRequest;
import com.auacm.api.model.request.UpdateTeamsRequest;
import com.auacm.database.model.*;

import java.util.List;
import java.util.Map;

public interface CompetitionService {
    boolean isInUpcomingCompetition(Problem problem);

    Competition getCompetitionByName(String name);

    Competition getCompetitionById(Long competitionId);

    Map<String, List<Competition>> getAllCompetitions();

    List<CompetitionUser> getRecentCompetitionsForUser(String username, int amount);

    boolean isUserRegistered(Long competitionId, String user);

    boolean isCurrentUserRegistered(Competition competition);

    CompetitionUser registerCurrentUser(long competitionId);

    List<CompetitionUser> registerUsers(long competitionId, List<String> userNames);

    void unregisterUsers(long competitionId, List<String> userNames);

    Competition createCompetition(CreateCompetitionRequest newCompetition);

    Competition updateCompetition(long competitionId, CreateCompetitionRequest newCompetition);

    void deleteCompetition(long competitionId);

    Competition updateCompetitionTeams(long competitionId, UpdateTeamsRequest competitionTeams);

    Scoreboard getScoreboard(Competition competition, Map<Long, Problem> problems);

    Map<String, List<User>> getTeamList(Competition competition);

    Map<String, List<User>> getTeams(long competitionId);

    void broadcastCompetitionUsers(long competitionId, Map<String, List<User>> teamMap);

    void broadcastCompetitionUsers(long competitionId);
}
