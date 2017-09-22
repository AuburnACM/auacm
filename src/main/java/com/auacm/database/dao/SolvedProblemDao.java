package com.auacm.database.dao;

import com.auacm.database.model.SolvedProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolvedProblemDao extends JpaRepository<SolvedProblem, Long> {
    List<SolvedProblem> findByUsernameIgnoreCase(String username);
}
