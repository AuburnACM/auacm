package io.github.auburnacm.auacm.database.dao;

import io.github.auburnacm.auacm.database.model.Problem;

import java.util.List;

public interface ProblemDao {
    void addProblem(Problem object);

    List<Problem> getProblems();

    Problem getProblem(String parameter, Object object);

    void updateProblem(Problem object);

    void deleteProblem(Problem object);
}
