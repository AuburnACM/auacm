package com.auacm.service;

import com.auacm.api.model.CompetitionTeams;
import com.auacm.api.model.CreateCompetition;
import com.auacm.api.model.SimpleTeam;
import com.auacm.api.proto.CompetitionOuterClass;
import com.auacm.database.dao.CompetitionDao;
import com.auacm.database.dao.CompetitionProblemDao;
import com.auacm.database.dao.CompetitionUserDao;
import com.auacm.database.model.*;
import com.auacm.exception.CompetitionNotFoundException;
import com.auacm.exception.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CompetitionServiceImpl implements CompetitionService {
    private final String LABELS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Autowired
    private CompetitionDao competitionDao;

    @Autowired
    private CompetitionProblemDao competitionProblemDao;

    @Autowired
    private ProblemService problemService;

    @Autowired
    private CompetitionUserDao competitionUserDao;

    @Autowired
    private UserService userService;

    @Autowired
    private SubmissionService submissionService;

    @Override
    public boolean isInUpcomingCompetition(Problem problem) {
        if (problem.getCompetitionId() != null && problem.getCompetitionId() > 0) {
            Competition competition = competitionDao.findOne(problem.getCompetitionId());
            long currentTime = System.currentTimeMillis() / 1000;
            if (competition.getStart() > currentTime) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Competition getCompetitionByName(String name) {
        try {
            return competitionDao.getCompetitionByName(name);
        } catch (JpaObjectRetrievalFailureException e) {
            throw new CompetitionNotFoundException();
        }
    }

    @Override
    public Competition getCompetitionById(Long id) {
        try {
            return competitionDao.getOne(id);
        } catch (JpaObjectRetrievalFailureException e) {
            throw new CompetitionNotFoundException();
        }
    }

    @Override
    public Map<String, List<Competition>> getAllCompetitions() {
        List<Competition> competitions = competitionDao.findAll();
        HashMap<String, List<Competition>> competitionMap = new HashMap<>();
        competitionMap.put("ongoing", new ArrayList<>());
        competitionMap.put("upcoming", new ArrayList<>());
        competitionMap.put("past", new ArrayList<>());
        long currentTime = System.currentTimeMillis() / 1000;
        for (Competition c : competitions) {
            if (currentTime < c.getStart()) {
                competitionMap.get("upcoming").add(c);
            } else if (currentTime >= c.getStart() && currentTime < c.getStop()) {
                competitionMap.get("ongoing").add(c);
            } else {
                competitionMap.get("past").add(c);
            }
        }
        return competitionMap;
    }

    @Override
    public boolean isUserRegistered(Long competitionId, String user) {
        try {
            CompetitionUser competitionUser = competitionUserDao.findOneByUsernameAndCid(user, competitionId);
            return competitionUser != null;
        } catch (JpaObjectRetrievalFailureException e) {
            return false;
        }
    }

    @Override
    public boolean isCurrentUserRegistered(Competition competition) {
        boolean registered = false;
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserPrincipal) {
            User user = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
            for (CompetitionUser competitionUser : competition.getCompetitionUsers()) {
                if (competitionUser.getUsername().equals(user.getUsername())) {
                    registered = true;
                    break;
                }
            }
        }
        return registered;
    }

    @Override
    public CompetitionUser registerCurrentUser(long competitionId) {
        Competition competition = getCompetitionById(competitionId);
        User user = ((UserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        if (competition.isClosed()) {
            if (!user.isAdmin()) {
                throw new ForbiddenException("This is a closed competition. An admin must register you.");
            }
        }
        CompetitionUser temp = new CompetitionUser();
        temp.setCid(competition.getCid());
        temp.setUsername(user.getUsername());
        temp.setTeam(user.getDisplay());
        saveCompetitionUsers(Collections.singletonList(temp));
        return temp;
    }

    @Override
    public List<CompetitionUser> registerUsers(long competitionId, List<String> userNames) {
        try {
            ArrayList<CompetitionUser> users = new ArrayList<>();
            for (String user : userNames) {
                User userData = userService.getUser(user);
                if (userData != null && !isUserRegistered(competitionId, user)) {
                    CompetitionUser temp = new CompetitionUser();
                    temp.setCid(competitionId);
                    temp.setTeam(userData.getDisplay());
                    temp.setUsername(user);
                    users.add(temp);
                }
            }
            saveCompetitionUsers(users);
            return users;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Transactional
    public void saveCompetitionUsers(List<CompetitionUser> list) {
        competitionUserDao.save(list);
    }

    @Override
    @Transactional
    public void unregisterUsers(long competitionId, List<String> userNames) {
        Competition competition = getCompetitionById(competitionId);
        for (String user : userNames) {
            competitionUserDao.deleteOneByUsernameAndCid(user, competitionId);
        }
    }

    @Override
    @Transactional
    public Competition createCompetition(CreateCompetition newCompetition) {
        Competition competition = new Competition();
        competition.setClosed(newCompetition.isClosed());
        competition.setName(newCompetition.getName());
        competition.setStart(newCompetition.getStartTime());
        competition.setStop(newCompetition.getStartTime() + newCompetition.getLength());
        competition = competitionDao.save(competition);

        ArrayList<CompetitionProblem> problems = new ArrayList<>();
        int index = 0;
        for (Long id : newCompetition.getProblems()) {
            Problem problem = problemService.getProblemForPid(id);
            CompetitionProblem tempProblem = new CompetitionProblem();
            tempProblem.setCid(competition.getCid());
            tempProblem.setLabel(LABELS.charAt(index) + "");
            tempProblem.setPid(problem.getPid());
            problems.add(tempProblem);
            index++;
        }
        competitionProblemDao.save(problems);
        competition.setCompetitionProblems(problems);
        if (newCompetition.getUserNames() != null) {
            List<CompetitionUser> users = registerUsers(competition.getCid(), newCompetition.getUserNames());
            competition.setCompetitionUsers(users);
        }
        return competition;
    }

    @Override
    @Transactional
    public Competition updateCompetition(long competitionId, CreateCompetition newCompetition) {
        Competition competition = getCompetitionById(competitionId);
        if (newCompetition.getName() != null) {
            competition.setName(newCompetition.getName());
        }
        if (newCompetition.getStartTime() != null) {
            long length = competition.getStop() - competition.getStart();
            competition.setStart(newCompetition.getStartTime());
            competition.setStop(competition.getStart() + length);
        }
        if (newCompetition.getLength() != null) {
            competition.setStop(competition.getStart() + newCompetition.getLength());
        }
        if (newCompetition.isClosed() != null) {
            competition.setClosed(newCompetition.isClosed());
        }
        if (newCompetition.getUserNames() != null) {
            List<CompetitionUser> users = competition.getCompetitionUsers();
            ArrayList<String> removeUsers = new ArrayList<>();
            ArrayList<String> newUsers = new ArrayList<>(newCompetition.getUserNames());
            // Find the difference between the current users and users to be added/removed
            for (CompetitionUser user : users) {
                if (!newCompetition.getUserNames().contains(user.getUsername())) {
                    removeUsers.add(user.getUsername());
                } else {
                    newUsers.remove(user.getUsername());
                }
            }
            // Remove users from competition
            for (String userName : removeUsers) {
                CompetitionUser temp = null;
                for (CompetitionUser user : competition.getCompetitionUsers()) {
                    if (user.getUsername().equals(userName)) {
                        temp = user;
                        break;
                    }
                }
                if (temp != null) {
                    competition.getCompetitionUsers().remove(temp);
                }
            }
            unregisterUsers(competition.getCid(), removeUsers);
            competition.getCompetitionUsers().addAll(registerUsers(competition.getCid(), newUsers));
        }
        if (newCompetition.getProblems() != null) {
            competitionProblemDao.deleteAllByCid(competitionId);
            ArrayList<CompetitionProblem> problems = new ArrayList<>();
            int index = 0;
            for (Long id : newCompetition.getProblems()) {
                Problem problem = problemService.getProblemForPid(id);
                CompetitionProblem tempProblem = new CompetitionProblem();
                tempProblem.setCid(competition.getCid());
                tempProblem.setLabel(LABELS.charAt(index) + "");
                tempProblem.setPid(problem.getPid());
                problems.add(tempProblem);
                index++;
            }
            competition.getCompetitionProblems().clear();
            competitionProblemDao.save(problems);
            competition.getCompetitionProblems().addAll(problems);
        }
        return competition;
    }

    @Override
    @Transactional
    public void deleteCompetition(long competitionId) {
        Competition competition = getCompetitionById(competitionId);
        competitionProblemDao.deleteAllByCid(competitionId);
        competitionUserDao.deleteAllByCid(competitionId);
        competitionDao.delete(competitionId);
    }

    @Override
    public Competition updateCompetitionTeams(long competitionId, CompetitionTeams competitionTeams) {
        Competition competition = getCompetitionById(competitionId);
        ArrayList<CompetitionUser> toUpdate = new ArrayList<>();
        ArrayList<CompetitionUser> toDelete = new ArrayList<>();
        // Lets iterate through existing users and update them.
        // We will also remove the ones we find from the competitionTeams object
        // That way we can iterate over the ones we have yet to do and add them to the toUpdate object
        for (CompetitionUser user : competition.getCompetitionUsers()) {
            boolean found = false;
            for (String teams : new ArrayList<>(competitionTeams.getTeams().keySet())) {
                List<SimpleTeam> teamList = new ArrayList<>(competitionTeams.getTeams().get(teams));
                for (SimpleTeam team : teamList) {
                    if (team.getUsername().equals(user.getUsername())) {
                        if (!user.getTeam().equals(teams)) {
                            user.setTeam(teams);
                            toUpdate.add(user);
                            found = true;
                            competitionTeams.getTeams().get(teams).remove(team);
                            if (competitionTeams.getTeams().get(teams).size() == 0) {
                                competitionTeams.getTeams().remove(teams);
                            }
                            break;
                        }
                    }
                }
                if (found) {
                    break;
                }
            }
            if (!found) {
                toDelete.add(user);
            }
        }
        for (String teamName : competitionTeams.getTeams().keySet()) {
            List<SimpleTeam> teamList = competitionTeams.getTeams().get(teamName);
            for (SimpleTeam team : teamList) {
                CompetitionUser newUser = new CompetitionUser();
                newUser.setTeam(teamName);
                newUser.setUsername(team.getUsername());
                newUser.setCid(competitionId);
                toUpdate.add(newUser);
            }
        }
        deleteCompetitionTeams(toDelete);
        competition.setCompetitionUsers(updateCompetitionTeams(updateCompetitionTeams(toUpdate)));
        return competition;
    }

    @Transactional
    public List<CompetitionUser> updateCompetitionTeams(List<CompetitionUser> teams) {
        return competitionUserDao.save(teams);
    }

    @Transactional
    public void deleteCompetitionTeams(List<CompetitionUser> teams) {
        competitionUserDao.delete(teams);
    }

    @Override
    public Scoreboard getScoreboard(Competition competition, Map<Long, Problem> problems) {
        HashMap<String, ScoreboardTeam> teamMap = new HashMap<>();
        HashMap<String, List<Submission>> submissionMap = new HashMap<>();
        Scoreboard scoreboard = new Scoreboard();

        // First lets create the teams
        for (CompetitionUser competitionUser : competition.getCompetitionUsers()) {
            if (!teamMap.containsKey(competitionUser.getTeam())) {
                ScoreboardTeam team = new ScoreboardTeam(competitionUser.getTeam());
                teamMap.put(competitionUser.getTeam(), team);
                scoreboard.addTeam(team);
            }
            User user = userService.getUser(competitionUser.getUsername());
            teamMap.get(competitionUser.getTeam()).addUser(user);
            long start = competition.getStart();
            long stop = competition.getStop();
            List<Submission> submissions = submissionService.getAllSubmissionsForUserNameBetween(
                    user.getUsername(), start, stop);
            if (submissions != null) {
                submissionMap.put(user.getUsername(), submissions);
            }
        }

        // Now we iterate through the teams and find the submissions for each user for every problem
        for (ScoreboardTeam team : teamMap.values()) {
            // Iterate over every problem
            for (CompetitionProblem competitionProblem : competition.getCompetitionProblems()) {
                ScoreboardProblem scoreboardProblem = new ScoreboardProblem(competitionProblem, problems.get(competitionProblem.getPid()));
                // Iterate over every person on the team. We will get all of the submissions and find the status
                for (User user : team.getUsers()) {
                    if (submissionMap.containsKey(user.getUsername())) {
                        // Each list of submissions is ordered by the submit time
                        for (Submission submission : submissionMap.get(user.getUsername())) {
                            if (submission.getPid() == scoreboardProblem.getProblem().getPid()) {
                                scoreboardProblem.incrementSubmitCount();
                                scoreboardProblem.setSubmitTime(submission.getSubmitTime());
                                if (submission.getResult().equals("correct")) {
                                    scoreboardProblem.setStatus("correct");
                                    break;
                                } else if (submission.getResult().equals("running")) {
                                    scoreboardProblem.setStatus("running");
                                } else {
                                    scoreboardProblem.setStatus("incorrect");
                                }
                            }
                        }
                    }
                }
                team.addProblem(scoreboardProblem.getProblem().getPid() + "", scoreboardProblem);
            }
        }
        return scoreboard;
    }

    @Override
    public CompetitionOuterClass.SingleCompetitionWrapper getCompetitionResponse(Competition competition) {
        CompetitionOuterClass.SingleCompetition.Builder builder = CompetitionOuterClass.SingleCompetition.newBuilder()
                .setCompetition(CompetitionOuterClass.Competition.newBuilder().setCid(competition.getCid())
                        .setClosed(competition.isClosed()).setLength(competition.getStop() - competition.getStart())
                        .setStartTime(competition.getStart()).setRegistered(isCurrentUserRegistered(competition))
                        .setName(competition.getName()));
        HashMap<Long, Problem> problems = new HashMap<>();
        for (CompetitionProblem competitionProblem : competition.getCompetitionProblems()) {
            Problem temp = problemService.getProblemForPid(competitionProblem.getPid());
            problems.put(temp.getPid(), temp);
            builder.putCompProblems(competitionProblem.getLabel(), CompetitionOuterClass.CompetitionProblem.newBuilder()
                    .setName(temp.getName()).setPid(temp.getPid()).setShortName(temp.getShortName()).build());
        }
        Scoreboard scoreboard = getScoreboard(competition, problems);
        for (ScoreboardTeam team : scoreboard.getTeams()) {
            CompetitionOuterClass.CompetitionTeam.Builder teamBuilder = CompetitionOuterClass.CompetitionTeam.newBuilder();
            teamBuilder.setName(team.getName());
            for (User user : team.getUsers()) {
                teamBuilder.addDisplayNames(user.getDisplay());
                teamBuilder.addUsers(user.getUsername());
            }
            for (ScoreboardProblem scoreboardProblem : team.getProblemsOrderByLabel()) {
                teamBuilder.putProblemData(scoreboardProblem.getProblem().getPid() + "",
                        CompetitionOuterClass.CompetitionTeamProblem.newBuilder()
                                .setLabel(scoreboardProblem.getCompetitionProblem().getLabel())
                                .setStatus(scoreboardProblem.getStatus())
                                .setSubmitCount(scoreboardProblem.getSubmitCount())
                                .setSubmitTime(scoreboardProblem.getSubmitTime()).build());
            }
            builder.addTeams(teamBuilder);
        }
        return CompetitionOuterClass.SingleCompetitionWrapper.newBuilder().setData(builder).build();
    }

    @Override
    public CompetitionOuterClass.CompetitionListWrapper getCompetitionListResponse(Map<String, List<Competition>> competitions) {
        CompetitionOuterClass.CompetitionList.Builder builder = CompetitionOuterClass.CompetitionList.newBuilder();
        for (Competition c : competitions.get("ongoing")) {
            builder.addOngoing(getCompetition(c));
        }
        for (Competition c : competitions.get("upcoming")) {
            builder.addUpcoming(getCompetition(c));
        }
        for (Competition c : competitions.get("past")) {
            builder.addPast(getCompetition(c));
        }
        return CompetitionOuterClass.CompetitionListWrapper.newBuilder().setData(builder).build();
    }

    @Override
    public CompetitionOuterClass.TeamList getTeamList(Competition competition) {
        HashMap<String, ScoreboardTeam> teams = getTeamMap(competition);
        CompetitionOuterClass.TeamList.Builder builder = CompetitionOuterClass.TeamList.newBuilder();
        for (Map.Entry<String, ScoreboardTeam> team : teams.entrySet()) {
            CompetitionOuterClass.TeamList.TeamListWrapper.Builder tempBuilder = CompetitionOuterClass.TeamList.TeamListWrapper.newBuilder();
            for (User user : team.getValue().getUsers()) {
                tempBuilder.addList(CompetitionOuterClass.SimpleTeam.newBuilder().setDisplay(user.getDisplay())
                        .setUsername(user.getUsername()));
            }
            builder.putData(team.getKey(), tempBuilder.build());
        }
        return builder.build();
    }

    private CompetitionOuterClass.Competition.Builder getCompetition(Competition competition) {
        return CompetitionOuterClass.Competition.newBuilder().setCid(competition.getCid())
                .setClosed(competition.isClosed()).setLength(competition.getStop() - competition.getStart())
                .setStartTime(competition.getStart()).setRegistered(isCurrentUserRegistered(competition))
                .setName(competition.getName());
    }

    private HashMap<String, ScoreboardTeam> getTeamMap(Competition competition) {
        HashMap<String, ScoreboardTeam> teamMap = new HashMap<>();
        for (CompetitionUser competitionUser : competition.getCompetitionUsers()) {
            if (!teamMap.containsKey(competitionUser.getTeam())) {
                ScoreboardTeam team = new ScoreboardTeam(competitionUser.getTeam());
                teamMap.put(competitionUser.getTeam(), team);
            }
            User user = userService.getUser(competitionUser.getUsername());
            teamMap.get(competitionUser.getTeam()).addUser(user);
        }
        return teamMap;
    }
}
