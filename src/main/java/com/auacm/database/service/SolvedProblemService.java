package com.auacm.database.service;

import com.auacm.database.model.SolvedProblem;

import java.util.List;

public interface SolvedProblemService {
    List<SolvedProblem> getProblemsForUser(String username);

    void addSolvedProblem(SolvedProblem problem);

    void addSolvedProblem(long problemId, String username);
}
