package com.auacm.database.dao;

import com.auacm.database.model.ProblemData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemDataDao extends JpaRepository<ProblemData, Long> {
}
