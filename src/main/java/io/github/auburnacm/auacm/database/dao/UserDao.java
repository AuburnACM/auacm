package io.github.auburnacm.auacm.database.dao;

import io.github.auburnacm.auacm.database.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao extends JpaRepository<User, String> {
    List<User> findByUsername(String username);
}
