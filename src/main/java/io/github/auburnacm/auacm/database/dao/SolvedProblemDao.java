package io.github.auburnacm.auacm.database.dao;

import io.github.auburnacm.auacm.database.model.SolvedProblem;

import java.util.List;

/**
 * Created by Mac on 9/17/17.
 */
public interface SolvedProblemDao extends BaseDao<SolvedProblem> {
    void addSolvedProblem(SolvedProblem object);

    List<SolvedProblem> getSolvedProblems();

    List<SolvedProblem> getSolvedProblems(String parameter, Object object);

    SolvedProblem getSolvedProblem(String parameter, Object object);

    void updateSolvedProblem(SolvedProblem object);

    void deleteSolvedProblem(SolvedProblem object);
}
