package com.auacm.database.dao;

import com.auacm.database.model.CompetitionProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetitionProblemDao extends JpaRepository<CompetitionProblem, Long> {
    CompetitionProblem findByPid(long pid);
}
