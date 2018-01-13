package com.auacm.database.dao;

import com.auacm.database.model.Submission;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionDao extends JpaRepository<Submission, Long> {
    List<Submission> findAllBySubmitTimeGreaterThanEqualAndSubmitTimeLessThanOrderBySubmitTime(Long first, Long second);

    List<Submission> findAllByUsernameAndSubmitTimeGreaterThanEqualAndSubmitTimeLessThanOrderBySubmitTime(String username, Long first, Long second);

    List<Submission> findAllByUsernameOrderByJob(String username, Pageable pageable);

    @Query("SELECT COUNT(DISTINCT s.pid) FROM Submission s WHERE s.username=:username AND s.result=:result")
    int countDistinctByUsernameAndResult(@Param("username") String username, @Param("result") String result);
}
