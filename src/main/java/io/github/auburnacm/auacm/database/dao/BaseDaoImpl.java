package io.github.auburnacm.auacm.database.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class BaseDaoImpl<T> implements BaseDao<T> {

    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    protected SessionFactory sessionFactory;

    private Class<T> tClass;

    public BaseDaoImpl(Class<T> tClass) {
        this.tClass = tClass;
    }


    @Override
    public void addEntity(T object) {
        entityManager.persist(object);
    }

    @Override
    public List<T> getEntities() {
        CriteriaQuery<T> query = entityManager.getCriteriaBuilder().createQuery(tClass);
        Root<T> from = query.from(tClass);
        query.select(from);
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public T getEntity(String parameter, Object object) {
        CriteriaQuery<T> query = entityManager.getCriteriaBuilder().createQuery(tClass);
        Root<T> from = query.from(tClass);
        query.select(from);
        query.where(entityManager.getCriteriaBuilder().equal(from.get(parameter), object));
        try {
            return entityManager.createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void updateEntity(T object) {
        sessionFactory.getCurrentSession().update(object);
    }

    @Override
    public void deleteEntity(T object) {
        sessionFactory.getCurrentSession().delete(object);
    }
}
