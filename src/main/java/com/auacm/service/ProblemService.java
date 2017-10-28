package com.auacm.service;

import com.auacm.database.model.Problem;

import java.util.List;

public interface ProblemService {
    Problem addProblem(Problem problem);

    //TODO implement CreateProblem Problem addProblem(CreateProblem problem, String username);

    Problem updateProblem(Problem problem);

    //TODO implement UpdateProblem Problem updateProblem(UpdateProblem problem, long pid);

    void deleteProblem(Problem problem);

    List<Problem> getAllProblems();

    Problem getProblem(String identifier);

    Problem getProblemForPid(long pid);

    Problem getProblemForShortName(String shortName);
    //TODO implement Problem.ProblemResponseWrapper Problem.ProblemResponseWrapper getResponseForProblem(Problem problem, User user);

    com.auacm.api.proto.Problem.ProblemListWrapper getProblemListResponse(List<Problem> problems);

    com.auacm.api.proto.Problem.ProblemWrapper getProblemResponse(Problem problem);
}
