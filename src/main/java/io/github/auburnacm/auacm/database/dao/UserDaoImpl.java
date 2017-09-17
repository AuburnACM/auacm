package io.github.auburnacm.auacm.database.dao;

import io.github.auburnacm.auacm.database.model.User;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class UserDaoImpl extends BaseDaoImpl<User> implements UserDao {
    public UserDaoImpl(EntityManager entityManager, SessionFactory sessionFactory) {
        super(User.class, entityManager, sessionFactory);
    }

    @Override
    public void addUser(User object) {
        addEntity(object);
    }

    @Override
    public List<User> getUsers() {
        return getEntities();
    }

    @Override
    public User getUser(String parameter, Object object) {
        return getEntity(parameter, object);
    }

    @Override
    public void updateUser(User object) {
        updateEntity(object);
    }

    @Override
    public void deleteUser(User object) {
        deleteEntity(object);
    }
}
