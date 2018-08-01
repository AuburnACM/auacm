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

    /**
     * Selects all submissions for a competition in ascending order by the team name, then the label, and lastly the
     * time submitted.
     *
     * The first item in the list is the submission.
     * The second item in the list is the competition problem.
     * The third item in the list is the competition user
     * @param cid - the id of the competition
     * @return a list of submissions
     */
    @Query("select sub, compprob, compuser from Submission sub " +
            "join CompetitionUser compuser on compuser.user.username = sub.user.username " +
            "join Competition comp on comp.cid = compuser.competition.cid " +
            "join CompetitionProblem compprob on compprob.problem.pid = sub.pid " +
            "where comp.cid = :cid and sub.submitTime >= comp.start and sub.submitTime < comp.stop " +
            "order by compuser.team, compprob.label, sub.submitTime asc")
    List<Object[]> findAllByCid(@Param("cid") Long cid);
}
