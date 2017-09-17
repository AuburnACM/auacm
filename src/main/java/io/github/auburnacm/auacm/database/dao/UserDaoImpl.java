package io.github.auburnacm.auacm.database.dao;

import io.github.auburnacm.auacm.database.model.User;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {
    private BaseDao<User> baseDao;

    public UserDaoImpl(EntityManager entityManager, SessionFactory sessionFactory) {
        baseDao = new BaseDaoImpl<>(User.class, entityManager, sessionFactory);
    }

    @Override
    public void addUser(User object) {
        baseDao.addEntity(object);
    }

    @Override
    public List<User> getUsers() {
        return baseDao.getEntities();
    }

    @Override
    public User getUser(String parameter, Object object) {
        return baseDao.getEntity(parameter, object);
    }

    @Override
    public void updateUser(User object) {
        baseDao.updateEntity(object);
    }

    @Override
    public void deleteUser(User object) {
        baseDao.deleteEntity(object);
    }
}
