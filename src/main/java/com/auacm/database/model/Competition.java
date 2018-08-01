package com.auacm.database.model;

import com.auacm.api.model.BasicUser;
import com.auacm.api.model.CompetitionTeam;
import com.auacm.api.model.request.CreateCompetitionRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "comp_names")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Proxy(lazy = false)
public class Competition implements Serializable {
    @Transient
    private final String LABELS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cid")
    private Long cid;

    private String name;

    private Long start;

    private Long stop;

    private Boolean closed;

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<CompetitionProblem> competitionProblems;

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<CompetitionUser> competitionUsers;

    @Transient
    private Map<String, CompetitionTeam> teams;

    @Transient
    private Map<String, com.auacm.api.model.CompetitionProblem> problems;


    public Competition(CreateCompetitionRequest competitionRequest) {
        this.name = competitionRequest.getName();
        this.start = competitionRequest.getStartTime();
        this.stop = competitionRequest.getStartTime() + competitionRequest.getLength();
        this.closed = competitionRequest.getClosed();
        this.competitionProblems = new ArrayList<>();
        this.competitionUsers = new ArrayList<>();
    }

    public void addUser(CompetitionUser competitionUser) {
        this.competitionUsers.add(competitionUser);
    }

    public void addProblem(CompetitionProblem competitionProblem) {
        this.competitionProblems.add(competitionProblem);
    }

    public int getUserCountForTeam(String teamName) {
        int count = 0;
        for (CompetitionUser user : competitionUsers) {
            if (user.getTeam().equals(teamName)) {
                count++;
            }
        }
        return count;
    }

    public boolean registerUser(User user) {
        boolean found = false;
        for (CompetitionUser competitionUser : competitionUsers) {
            if (competitionUser.getUser().getUsername().equals(user.getUsername())) {
                found = true;
                break;
            }
        }
        if (!found) {
            competitionUsers.add(new CompetitionUser(user.getUsername(), user.getDisplayName(), this));
            return true;
        } else {
            return false;
        }
    }

    public boolean unregisterUser(String username) {
        CompetitionUser user = null;
        for (CompetitionUser competitionUser : competitionUsers) {
            if (competitionUser.getUser().getUsername().equals(username)) {
                user = competitionUser;
                break;
            }
        }
        if (user != null) {
            competitionUsers.remove(user);
            return true;
        }
        return false;
    }

    public boolean unregisterUsers(List<String> users) {
        List<CompetitionUser> copy = new ArrayList<>(competitionUsers);
        boolean found = false;
        for (CompetitionUser user : copy) {
            if (users.contains(user.getUser().getUsername())) {
                competitionUsers.remove(user);
                found = true;
            }
        }
        return found;
    }

    public void unregisterUser(User user) {
        unregisterUser(user.getUsername());
    }

    public CompetitionUser getUserWithUsername(String username) {
        for (CompetitionUser user : competitionUsers) {
            if (user.getUser().getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public void update(CreateCompetitionRequest competitionRequest) {
        if (competitionRequest.getName() != null) {
            this.name = competitionRequest.getName();
        }
        if (competitionRequest.getStartTime() != null) {
            Long length = this.stop - this.start;
            this.start = competitionRequest.getStartTime();
            this.stop = this.start + length;
        }
        if (competitionRequest.getLength() != null) {
            this.stop = this.start + competitionRequest.getLength();
        }
        if (competitionRequest.getClosed() != null) {
            this.closed = competitionRequest.getClosed();
        }
    }

    public void updateUsers(List<User> users) {
        List<CompetitionUser> compUserCopy = new ArrayList<>(competitionUsers);
        // Remove missing users first
        for (CompetitionUser competitionUser : compUserCopy) {
            if (users.stream().noneMatch(user -> user.getUsername().equals(competitionUser.getUser().getUsername()))) {
                competitionUsers.remove(competitionUser);
            }
        }
        // Add new users
        for (User user : users) {
            if (competitionUsers.stream().noneMatch(compUser -> user.getUsername()
                    .equals(compUser.getUser().getUsername()))) {
                competitionUsers.add(new CompetitionUser(user.getUsername(), user.getDisplayName(), this));
            }
        }
    }

    public void updateProblems(List<Problem> problems) {
        List<CompetitionProblem> compProbCopy = new ArrayList<>(competitionProblems);
        // Remove missing problems first
        for (CompetitionProblem competitionProblem : compProbCopy) {
            if (problems.stream().noneMatch(problem -> problem.getPid()
                    .equals(competitionProblem.getProblem().getPid()))) {
                competitionProblems.remove(competitionProblem);
            }
        }
        // Add new problems
        for (Problem problem : problems) {
            if (competitionProblems.stream().noneMatch(compProblem -> compProblem
                    .getProblem().getPid().equals(problem.getPid()))) {
                competitionProblems.add(new CompetitionProblem("", this, problem));
            }
        }
        // Reset labels
        int index = 0;
        for (CompetitionProblem problem : competitionProblems) {
            problem.setLabel(LABELS.charAt(index) + "");
            index++;
        }
    }

    public void updateTeams(Map<String, List<BasicUser>> newTeams) {
        List<CompetitionUser> toRemove = new ArrayList<>();
        // Update users
        for (CompetitionUser competitionUser : competitionUsers) {
            boolean found = false;
            for (String teamName : newTeams.keySet()) {
                List<BasicUser> teamList = newTeams.get(teamName);
                if(teamList.stream().anyMatch(user -> user.getUsername()
                        .equals(competitionUser.getUser().getUsername()))) {
                    competitionUser.setTeam(teamName);
                    found = true;
                }
            }
            if (!found) {
                toRemove.add(competitionUser);
            }
        }
        // Remove missing teams
        for (CompetitionUser competitionUser : toRemove) {
            competitionUsers.remove(competitionUser);
        }
    }

    public void configureTeams(boolean showProblems) {
        if (problems == null) {
            this.problems = new HashMap<>();
            this.teams = new HashMap<>();
            if (showProblems) {
                for (com.auacm.database.model.CompetitionProblem problem : competitionProblems) {
                    problems.put(problem.getLabel(), new com.auacm.api.model.CompetitionProblem(problem));
                }
            }
            for (CompetitionUser competitionUser : competitionUsers) {
                if (!teams.containsKey(competitionUser.getTeam())) {
                    teams.put(competitionUser.getTeam(), new CompetitionTeam());
                }
                teams.get(competitionUser.getTeam()).addUser(competitionUser.getUser());
                if (showProblems) {
                    teams.get(competitionUser.getTeam()).initializeProblems(competitionProblems);
                }
            }
        }
    }

    public void setProblemData(List<Object[]> submissions) {
        if (teams == null) {
            this.teams = new HashMap<>();
        }
        for (Object[] tuple : submissions) {
            Submission submission = (Submission) tuple[0];
            com.auacm.database.model.CompetitionProblem competitionProblem = (com.auacm.database.model.CompetitionProblem) tuple[1];
            CompetitionUser user = (CompetitionUser) tuple[2];
            teams.get(user.getTeam()).addSubmission(competitionProblem.getLabel(), submission);
        }
    }
}
