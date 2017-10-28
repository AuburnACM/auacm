package com.auacm.service;

import com.auacm.database.model.Problem;
import com.auacm.database.model.SolvedProblem;
import com.auacm.database.model.User;

import java.util.List;

public interface SolvedProblemService {
    List<SolvedProblem> getProblemsForUser(String username);

    void addSolvedProblem(SolvedProblem problem);

    void addSolvedProblem(long problemId, String username);

    boolean hasSolved(Problem problem);

    boolean hasSolved(User user, Problem problem);
}
