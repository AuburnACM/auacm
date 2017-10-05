package com.auacm.database.dao;

import com.auacm.database.model.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetitionDao extends JpaRepository<Competition, Long> {
}
