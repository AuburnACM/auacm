package io.github.auburnacm.auacm.database.dao;

import java.util.List;

public interface BaseDao<T> {
    void addEntity(T object);

    List<T> getEntities();

    List<T> getEntities(String parameter, Object object);

    T getEntity(String parameter, Object object);

    void updateEntity(T object);

    void deleteEntity(T object);

}
