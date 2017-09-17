package io.github.auburnacm.auacm.database.dao;

import io.github.auburnacm.auacm.database.model.Problem;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ProblemDaoImpl extends BaseDaoImpl<Problem> implements ProblemDao {

    public ProblemDaoImpl(EntityManager entityManager, SessionFactory sessionFactory) {
        super(Problem.class, entityManager, sessionFactory);
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
