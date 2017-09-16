package io.github.auburnacm.auacm.database.dao;

import io.github.auburnacm.auacm.model.Problem;

import java.util.List;

public interface ProblemDao extends BaseDao<Problem> {
    void addProblem(Problem object);

    List<Problem> getProblems();

    Problem getProblem(String parameter, Object object);

    void updateProblem(Problem object);

    void deleteProblem(Problem object);
}
