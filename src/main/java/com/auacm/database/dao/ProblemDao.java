package com.auacm.database.dao;

import com.auacm.database.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemDao extends JpaRepository<Problem, Long> {
    Problem findByShortNameIgnoreCase(String shortName);
}
