package io.github.auburnacm.auacm.database.dao;

import io.github.auburnacm.auacm.database.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemDao extends JpaRepository<Problem, Long> {
}
