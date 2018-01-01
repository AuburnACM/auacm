package com.auacm.database.dao;

import com.auacm.database.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionDao extends JpaRepository<Submission, Long> {
    List<Submission> findAllBySubmitTimeGreaterThanEqualAndSubmitTimeLessThanOrderBySubmitTime(Long first, Long second);

    List<Submission> findAllByUserNameAndSubmitTimeGreaterThanEqualAndSubmitTimeLessThanOrderBySubmitTime(String userName, Long first, Long second);
}
