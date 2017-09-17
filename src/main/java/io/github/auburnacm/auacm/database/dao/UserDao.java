package io.github.auburnacm.auacm.database.dao;

import io.github.auburnacm.auacm.database.model.User;

import java.util.List;

public interface UserDao extends BaseDao<User> {
    void addUser(User object);

    List<User> getUsers();

    User getUser(String parameter, Object object);

    void updateUser(User object);

    void deleteUser(User object);
}
