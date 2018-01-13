package com.auacm.service;

import com.auacm.api.model.CompetitionTeams;
import com.auacm.api.model.CreateCompetition;
import com.auacm.api.model.SimpleTeam;
import com.auacm.api.proto.CompetitionOuterClass;
import com.auacm.database.model.Competition;
import com.auacm.database.model.CompetitionUser;
import com.auacm.database.model.Problem;
import com.auacm.database.model.Scoreboard;

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

    Competition createCompetition(CreateCompetition newCompetition);

    Competition updateCompetition(long competitionId, CreateCompetition newCompetition);

    void deleteCompetition(long competitionId);

    Competition updateCompetitionTeams(long competitionId, CompetitionTeams competitionTeams);

    Scoreboard getScoreboard(Competition competition, Map<Long, Problem> problems);

    CompetitionOuterClass.SingleCompetitionWrapper getCompetitionResponse(Competition competition);

    CompetitionOuterClass.CompetitionListWrapper getCompetitionListResponse(Map<String, List<Competition>> competitions);

    CompetitionOuterClass.TeamList getTeamListResponse(Competition competition);

    Map<String, List<SimpleTeam>> getTeamList(Competition competition);

    void broadcastCompetitionUsers(long competitionId, Map<String, List<SimpleTeam>> teamMap);

    void broadcastCompetitionUsers(long competitionId);
}
