package com.auacm.service;

import com.auacm.api.model.CreateProblem;
import com.auacm.database.model.Problem;

import java.util.List;

public interface ProblemService {
    Problem createProblem(CreateProblem problem);

    Problem addProblem(Problem problem);

    Problem updateProblem(Problem problem);

    Problem updateProblem(String identifier, CreateProblem problem);

    void deleteProblem(Problem problem);

    void deleteProblem(String identifier);

    List<Problem> getAllProblems();

    Problem getProblem(String identifier);

    Problem getProblemForPid(long pid);

    Problem getProblemForShortName(String shortName);
    //TODO implement Problem.ProblemResponseWrapper Problem.ProblemResponseWrapper getResponseForProblem(Problem problem, User user);

    com.auacm.api.proto.Problem.ProblemListWrapper getProblemListResponse(List<Problem> problems);

    com.auacm.api.proto.Problem.ProblemWrapper getProblemResponse(Problem problem);
}
