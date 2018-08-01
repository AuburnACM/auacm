package com.auacm.database.dao;

import com.auacm.database.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProblemDao extends JpaRepository<Problem, Long> {
    Optional<Problem> findByShortNameIgnoreCase(String shortName);

    void deleteByShortName(String shortName);
}
