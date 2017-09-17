package io.github.auburnacm.auacm.database.dao;

import io.github.auburnacm.auacm.database.model.SolvedProblem;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class SolvedProblemDaoImpl extends BaseDaoImpl<SolvedProblem> implements SolvedProblemDao {

    public SolvedProblemDaoImpl(EntityManager entityManager, SessionFactory factory) {
        super(SolvedProblem.class, entityManager, factory);
    }

    @Override
    public void addSolvedProblem(SolvedProblem object) {
        addEntity(object);
    }

    @Override
    public List<SolvedProblem> getSolvedProblems() {
        return getEntities();
    }

    @Override
    public List<SolvedProblem> getSolvedProblems(String parameter, Object object) {
        return getEntities(parameter, object);
    }

    @Override
    public SolvedProblem getSolvedProblem(String parameter, Object object) {
        return getEntity(parameter, object);
    }

    @Override
    public void updateSolvedProblem(SolvedProblem object) {
        updateEntity(object);
    }

    @Override
    public void deleteSolvedProblem(SolvedProblem object) {
        deleteEntity(object);
    }
}
