package io.github.auburnacm.auacm.database.dao;

import io.github.auburnacm.auacm.model.Problem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProblemDaoImpl extends BaseDaoImpl<Problem> implements ProblemDao {

    public ProblemDaoImpl() {
        super(Problem.class);
    }

    @Override
    public void addProblem(Problem object) {
        addEntity(object);
    }

    @Override
    public List<Problem> getProblems() {
        return getEntities();
    }

    @Override
    public Problem getProblem(String parameter, Object object) {
        return getProblem(parameter, object);
    }

    @Override
    public void updateProblem(Problem object) {
        updateEntity(object);
    }

    @Override
    public void deleteProblem(Problem object) {
        deleteEntity(object);
    }
}
