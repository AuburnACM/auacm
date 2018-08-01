package com.auacm.service;

import com.auacm.api.model.request.CreateCompetitionRequest;
import com.auacm.api.model.request.UpdateTeamsRequest;
import com.auacm.database.dao.CompetitionDao;
import com.auacm.database.dao.CompetitionProblemDao;
import com.auacm.database.dao.CompetitionUserDao;
import com.auacm.database.model.*;
import com.auacm.exception.AlreadyRegisteredException;
import com.auacm.exception.CompetitionNotFoundException;
import com.auacm.exception.ForbiddenException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private Gson gson;

    @Override
    public boolean isInUpcomingCompetition(Problem problem) {
        if (problem.getCompetitionId() != null && problem.getCompetitionId() > 0) {
            Competition competition = competitionDao.getOne(problem.getCompetitionId());
            long currentTime = System.currentTimeMillis() / 1000;
            if (competition.getStart() > currentTime) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public Competition getCompetitionByName(String name) {
        Optional<Competition> optComp = competitionDao.getCompetitionByName(name);
        if (optComp.isPresent()) {
            Competition competition = optComp.get();
            configureProblems(competition);
            return competition;
        } else {
            throw new CompetitionNotFoundException();
        }
    }

    @Override
    @Transactional
    public Competition getCompetitionById(Long id) {
        Optional<Competition> optComp = competitionDao.findById(id);
        if (optComp.isPresent()) {
            Competition competition = optComp.get();
            configureProblems(competition);
            return competition;
        } else {
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
    public List<CompetitionUser> getRecentCompetitionsForUser(String username, int amount) {
        return competitionUserDao.findAllByUsernameOrderByCidDesc(username, PageRequest.of(0, amount));
    }

    @Override
    @Transactional
    public boolean isUserRegistered(Long competitionId, String user) {
        Optional<CompetitionUser> competitionUser = competitionUserDao
                .findOneByUserUsernameAndCompetitionCid(user, competitionId);
        return competitionUser.isPresent();
    }

    @Override
    public boolean isCurrentUserRegistered(Competition competition) {
        boolean registered = false;
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User) {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            for (CompetitionUser competitionUser : competition.getCompetitionUsers()) {
                if (competitionUser.getUser().getUsername().equals(user.getUsername())) {
                    registered = true;
                    break;
                }
            }
        }
        return registered;
    }

    @Override
    @Transactional
    public CompetitionUser registerCurrentUser(long competitionId) {
        Optional<Competition> competition = competitionDao.findById(competitionId);
        if (competition.isPresent()) {
            Competition comp = competition.get();
            User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (comp.getClosed() && !user.getAdmin()) {
                throw new ForbiddenException("This is a closed competition. An admin must register you.");
            } else {
                if (comp.registerUser(user)) {
                    competitionDao.save(comp);
                    broadcastCompetitionUsers(competitionId, getTeamList(comp));
                    return comp.getUserWithUsername(user.getUsername());
                } else {
                    throw new AlreadyRegisteredException();
                }
            }
        } else {
            throw new CompetitionNotFoundException("Failed to find a competition for that id.");
        }
    }

    @Override
    @Transactional
    public List<CompetitionUser> registerUsers(long competitionId, List<String> usernames) {
        Optional<Competition> competition = competitionDao.findById(competitionId);
        if (competition.isPresent()) {
            Competition comp = competition.get();
            boolean updated = false;
            for (String username : usernames) {
                User user = userService.getUser(username);
                if (user != null && comp.registerUser(user)) {
                    updated = true;
                }
            }
            if (updated) {
                competitionDao.save(comp);
                broadcastCompetitionUsers(competitionId, getTeamList(comp));
            }
            return comp.getCompetitionUsers();
        } else {
            throw new CompetitionNotFoundException("Failed to find a competition for that id.");
        }
    }

    @Transactional
    public List<CompetitionUser> saveCompetitionUsers(List<CompetitionUser> list) {
        return competitionUserDao.saveAll(list);
    }

    @Override
    @Transactional
    public void unregisterUsers(long competitionId, List<String> usernames) {
        Optional<Competition> competition = competitionDao.findById(competitionId);
        if (competition.isPresent()) {
            Competition comp = competition.get();
            if (comp.unregisterUsers(usernames)) {
                broadcastCompetitionUsers(competitionId, getTeamList(comp));
                competitionDao.save(comp);
            }
        } else {
            throw new CompetitionNotFoundException("Failed to find a competition for that id.");
        }
    }

    @Override
    @Transactional
    public Competition createCompetition(CreateCompetitionRequest newCompetition) {
        Competition competition = new Competition(newCompetition);
        for (String username : newCompetition.getUserNames()) {
            User user = userService.getUser(username);
            competition.addUser(new CompetitionUser(null, user.getDisplayName(), user, competition));
        }
        int index = 0;
        for (Long problemId : newCompetition.getProblems()) {
            competition.addProblem(new CompetitionProblem(LABELS.charAt(index) + "", competition,
                    problemService.getProblem(problemId + "")));
            index++;
        }
        competition = competitionDao.save(competition);
        configureProblems(competition);
        return competition;
    }

    @Override
    @Transactional
    public Competition updateCompetition(long competitionId, CreateCompetitionRequest newCompetition) {
        Optional<Competition> optComp = competitionDao.findById(competitionId);
        if (optComp.isPresent()) {
            Competition competition = optComp.get();
            competition.update(newCompetition);
            if (newCompetition.getUserNames() != null) {
                competition.updateUsers(newCompetition.getUserNames().stream()
                        .map(username -> userService.getUser(username))
                        .filter(Objects::nonNull).collect(Collectors.toList()));
            }
            if (newCompetition.getProblems() != null) {
                competition.updateProblems(newCompetition.getProblems().stream().map(problemId ->
                        problemService.getProblem(problemId + ""))
                        .filter(Objects::nonNull).collect(Collectors.toList()));
            }
            competition = competitionDao.save(competition);
            configureProblems(competition);
            broadcastCompetitionUsers(competitionId);
            return competition;
        } else {
            throw new CompetitionNotFoundException("Failed to find a competition for that id.");
        }
    }

    @Override
    @Transactional
    public void deleteCompetition(long competitionId) {
        Optional<Competition> competition = competitionDao.findById(competitionId);
        competition.ifPresent(competition1 -> competitionDao.delete(competition1));
    }

    @Override
    public Competition updateCompetitionTeams(long competitionId, UpdateTeamsRequest competitionTeams) {
        Optional<Competition> optCompetition = competitionDao.findById(competitionId);
        if (optCompetition.isPresent()) {
            Competition competition = optCompetition.get();
            competition.updateTeams(competitionTeams.getTeams());
            competition = competitionDao.save(competition);
            configureProblems(competition);
            broadcastCompetitionUsers(competitionId);
            return competition;
        } else {
            throw new CompetitionNotFoundException("Failed to find a competition for that id.");
        }
    }

    @Transactional
    public List<CompetitionUser> updateCompetitionTeams(List<CompetitionUser> teams) {
        Iterable<CompetitionUser> users = competitionUserDao.saveAll(teams);
        ArrayList<CompetitionUser> finalUsers = new ArrayList<>();
        for (CompetitionUser temp : users) {
            finalUsers.add(temp);
        }
        return finalUsers;
    }

    @Transactional
    public void deleteCompetitionTeams(List<CompetitionUser> teams) {
        competitionUserDao.deleteAll(teams);
    }

    @Override
    public Scoreboard getScoreboard(Competition competition, Map<Long, Problem> problems) {
        HashMap<String, ScoreboardTeam> teamMap = new HashMap<>();
        HashMap<String, List<Submission>> submissionMap = new HashMap<>();
        Scoreboard scoreboard = new Scoreboard();

//        // First lets create the teams
//        for (CompetitionUser competitionUser : competition.getCompetitionUsers()) {
//            if (!teamMap.containsKey(competitionUser.getTeam())) {
//                ScoreboardTeam team = new ScoreboardTeam(competitionUser.getTeam());
//                teamMap.put(competitionUser.getTeam(), team);
//                scoreboard.addTeam(team);
//            }
//            User user = userService.getUser(competitionUser.getUsername());
//            teamMap.get(competitionUser.getTeam()).addUser(user);
//            long start = competition.getStart();
//            long stop = competition.getStop();
//            List<Submission> submissions = submissionService.getAllSubmissionsForUsernameBetween(
//                    user.getUsername(), start, stop);
//            if (submissions != null) {
//                submissionMap.put(user.getUsername(), submissions);
//            }
//        }
//
//        // Now we iterate through the teams and find the submissions for each user for every problem
//        for (ScoreboardTeam team : teamMap.values()) {
//            // Iterate over every problem
//            for (CompetitionProblem competitionProblem : competition.getCompetitionProblems()) {
//                ScoreboardProblem scoreboardProblem = new ScoreboardProblem(competitionProblem, problems.get(competitionProblem.getPid()));
//                // Iterate over every person on the team. We will get all of the submissions and find the status
//                for (User user : team.getUsers()) {
//                    if (submissionMap.containsKey(user.getUsername())) {
//                        // Each list of submissions is ordered by the submit time
//                        for (Submission submission : submissionMap.get(user.getUsername())) {
//                            if (submission.getPid() == scoreboardProblem.getProblem().getPid()) {
//                                scoreboardProblem.incrementSubmitCount();
//                                scoreboardProblem.setSubmitTime(submission.getSubmitTime());
//                                if (submission.getResult().equals("correct")) {
//                                    scoreboardProblem.setStatus("correct");
//                                    break;
//                                } else if (submission.getResult().equals("running")) {
//                                    scoreboardProblem.setStatus("running");
//                                } else {
//                                    scoreboardProblem.setStatus("incorrect");
//                                }
//                            }
//                        }
//                    }
//                }
//                team.addProblem(scoreboardProblem.getProblem().getPid() + "", scoreboardProblem);
//            }
//        }
        return scoreboard;
    }

    @Override
    public Map<String, List<User>> getTeamList(Competition competition) {
        HashMap<String, ScoreboardTeam> teams = getTeamMap(competition);
        HashMap<String, List<User>> teamMap = new HashMap<>();
        for (Map.Entry<String, ScoreboardTeam> team : teams.entrySet()) {
            teamMap.put(team.getKey(), new ArrayList<>());
            for (User user : team.getValue().getUsers()) {
                teamMap.get(team.getKey()).add(user);
            }
        }
        return teamMap;
    }

    @Override
    public Map<String, List<User>> getTeams(long l) {
        Optional<Competition> competition = competitionDao.findById(l);
        if (competition.isPresent()) {
            return getTeamList(competition.get());
        } else {
            return new HashMap<>();
        }
    }

    @Override
    public void broadcastCompetitionUsers(long competitionId, Map<String, List<User>> teamMap) {
        JsonObject data = gson.toJsonTree(teamMap).getAsJsonObject();
        JsonObject message = new JsonObject();
        message.add("eventType", new JsonPrimitive("compUsers"));
        message.add("data", data);
        messagingTemplate.convertAndSend("/competitions/" + competitionId, message.toString());
    }

    @Override
    public void broadcastCompetitionUsers(long competitionId) {
        broadcastCompetitionUsers(competitionId, getTeams(competitionId));
    }

    private HashMap<String, ScoreboardTeam> getTeamMap(Competition competition) {
        HashMap<String, ScoreboardTeam> teamMap = new HashMap<>();
        for (CompetitionUser competitionUser : competition.getCompetitionUsers()) {
            if (!teamMap.containsKey(competitionUser.getTeam())) {
                ScoreboardTeam team = new ScoreboardTeam(competitionUser.getTeam());
                teamMap.put(competitionUser.getTeam(), team);
            }
            User user = userService.getUser(competitionUser.getUser().getUsername());
            teamMap.get(competitionUser.getTeam()).addUser(user);
        }
        return teamMap;
    }

    public void configureProblems(Competition competition) {
        if (competition.getStart() <= System.currentTimeMillis() / 1000) {
            competition.configureTeams(true);
            competition.setProblemData(submissionService.getSubmissionsForCid(competition.getCid()));
        } else {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                User user = (User) authentication.getPrincipal();
                competition.configureTeams(user.getAdmin());
                if (user.getAdmin()) {
                    competition.setProblemData(submissionService.getSubmissionsForCid(competition.getCid()));
                }
            } else {
                competition.configureTeams(false);
            }
        }
    }
}
