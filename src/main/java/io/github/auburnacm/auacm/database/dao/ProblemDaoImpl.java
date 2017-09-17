package io.github.auburnacm.auacm.database.dao;

import io.github.auburnacm.auacm.database.model.Problem;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class ProblemDaoImpl implements ProblemDao {
    private BaseDao<Problem> baseDao;

    public ProblemDaoImpl(EntityManager entityManager, SessionFactory sessionFactory) {
        baseDao = new BaseDaoImpl<>(Problem.class, entityManager, sessionFactory);
    }

    @Override
    public void addProblem(Problem object) {
        baseDao.addEntity(object);
    }

    @Override
    public List<Problem> getProblems() {
        return baseDao.getEntities();
    }

    @Override
    public Problem getProblem(String parameter, Object object) {
        return baseDao.getEntity(parameter, object);
    }

    @Override
    public void updateProblem(Problem object) {
        baseDao.updateEntity(object);
    }

    @Override
    public void deleteProblem(Problem object) {
        baseDao.deleteEntity(object);
    }
}
