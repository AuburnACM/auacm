package io.github.auburnacm.auacm.database.dao;

import java.util.List;

public interface BaseDao<T> {
    void addEntity(T object);

    List<T> getEntities();

    T getEntity(String parameter, Object object);

    void updateEntity(T object);

    void deleteEntity(T object);

}
