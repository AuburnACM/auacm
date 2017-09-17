package io.github.auburnacm.auacm.database.dao;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;


public class BaseDaoImpl<T> implements BaseDao<T> {

    protected EntityManager entityManager;

    protected SessionFactory sessionFactory;

    private Class<T> tClass;

    public BaseDaoImpl(Class<T> tClass, EntityManager entityManager, SessionFactory factory) {
        this.tClass = tClass;
        this.entityManager = entityManager;
        this.sessionFactory = factory;
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
    public List<T> getEntities(String parameter, Object object) {
        CriteriaQuery<T> query = entityManager.getCriteriaBuilder().createQuery(tClass);
        Root<T> from = query.from(tClass);
        query.select(from);
        query.select(from);
        query.where(entityManager.getCriteriaBuilder().equal(from.get(parameter), object));
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
        Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
        sessionFactory.getCurrentSession().update(object);
        transaction.commit();
    }

    @Override
    public void deleteEntity(T object) {
        Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
        sessionFactory.getCurrentSession().delete(object);
        transaction.commit();
    }
}
