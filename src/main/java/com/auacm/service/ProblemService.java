package com.auacm.service;

import com.auacm.api.model.request.CreateProblemRequest;
import com.auacm.api.model.response.CreateProblemResponse;
import com.auacm.database.model.Problem;

import java.util.List;

public interface ProblemService {
    CreateProblemResponse createProblem(CreateProblemRequest problem);

    Problem addProblem(Problem problem);

    Problem updateProblem(Problem problem);

    CreateProblemResponse updateProblem(String identifier, CreateProblemRequest problem);

    void deleteProblem(Problem problem);

    void deleteProblem(String identifier);

    List<Problem> getAllProblems();

    Problem getProblem(String identifier);

    Problem getProblemForPid(long pid);

    Problem getProblemForShortName(String shortName);
}
