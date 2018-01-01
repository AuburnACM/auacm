package com.auacm.database.dao;

import com.auacm.database.model.CompetitionUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompetitionUserDao extends JpaRepository<CompetitionUser, Long> {
    List<CompetitionUser> getAllByCid(Long cid);

    CompetitionUser findOneByUsername(String userName);

    CompetitionUser findOneByUsernameAndCid(String userName, Long cid);

    void deleteOneByUsernameAndCid(String userName, Long cid);

    void deleteAllByCid(Long cid);
}
