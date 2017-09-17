package io.github.auburnacm.auacm.database.dao;

import io.github.auburnacm.auacm.database.model.SolvedProblem;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class SolvedProblemDaoImpl implements SolvedProblemDao {
    private BaseDao<SolvedProblem> baseDao;

    public SolvedProblemDaoImpl(EntityManager entityManager, SessionFactory factory) {
        baseDao = new BaseDaoImpl<>(SolvedProblem.class, entityManager, factory);
    }

    @Override
    public void addSolvedProblem(SolvedProblem object) {
        baseDao.addEntity(object);
    }

    @Override
    public List<SolvedProblem> getSolvedProblems() {
        return baseDao.getEntities();
    }

    @Override
    public List<SolvedProblem> getSolvedProblems(String parameter, Object object) {
        return baseDao.getEntities(parameter, object);
    }

    @Override
    public SolvedProblem getSolvedProblem(String parameter, Object object) {
        return baseDao.getEntity(parameter, object);
    }

    @Override
    public void updateSolvedProblem(SolvedProblem object) {
        baseDao.updateEntity(object);
    }

    @Override
    public void deleteSolvedProblem(SolvedProblem object) {
        baseDao.deleteEntity(object);
    }
}
