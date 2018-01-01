package com.auacm.database.model;

import java.util.*;

public class ScoreboardTeam {
    private String name;
    private ArrayList<User> users;
    private HashMap<String, ScoreboardProblem> problems;

    public ScoreboardTeam() {
        this.users = new ArrayList<>();
        this.problems = new HashMap<>();
        this.name = "";
    }

    public ScoreboardTeam(String name) {
        this.users = new ArrayList<>();
        this.problems = new HashMap<>();
        this.name = name;
    }

    public void addUser(User user) {
        this.users.add(user);
    }

    public void addProblem(String name, ScoreboardProblem problem) {
        this.problems.put(name, problem);
    }

    public ScoreboardProblem getProblem(String name) {
        return problems.get(name);
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public HashMap<String, ScoreboardProblem> getProblems() {
        return problems;
    }

    public String getName() {
        return name;
    }

    public List<ScoreboardProblem> getProblemsOrderByLabel() {
        ArrayList<ScoreboardProblem> problems = new ArrayList<>(this.problems.values());
        problems.sort(Comparator.comparing(one -> one.getCompetitionProblem().getLabel()));
        return problems;
    }
}
