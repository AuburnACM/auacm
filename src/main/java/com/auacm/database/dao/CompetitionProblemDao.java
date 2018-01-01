package com.auacm.database.dao;

import com.auacm.database.model.CompetitionProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompetitionProblemDao extends JpaRepository<CompetitionProblem, Long> {
    CompetitionProblem findOneByPid(long pid);

    void deleteAllByCid(Long cid);

    List<CompetitionProblem> getAllByCid(Long cid);
}
