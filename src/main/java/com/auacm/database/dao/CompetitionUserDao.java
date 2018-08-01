package com.auacm.database.dao;

import com.auacm.database.model.CompetitionUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompetitionUserDao extends JpaRepository<CompetitionUser, Long> {
    List<CompetitionUser> getAllByCompetitionCid(Long cid);

    @Query(value = "select cu from CompetitionUser cu join cu.competition cn where cu.user.username=:username order by cn.start desc")
    List<CompetitionUser> findAllByUsernameOrderByCidDesc(@Param("username") String username, Pageable pageable);

    Optional<CompetitionUser> findOneByUserUsername(String userName);

    Optional<CompetitionUser> findOneByUserUsernameAndCompetitionCid(String userName, Long cid);

    void deleteOneByUserUsernameAndCompetitionCid(String userName, Long cid);

    void deleteAllByCompetitionCid(Long cid);
}
