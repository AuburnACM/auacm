package com.auacm.database.dao;

import com.auacm.database.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemDao extends JpaRepository<Problem, Long> {
    List<Problem> findAll();
    //List<Problem> findByPid(Long pid);

}
