package com.auacm.database.dao;

import com.auacm.database.model.SampleCase;
import com.auacm.database.model.SampleCasePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SampleCaseDao extends JpaRepository<SampleCase, SampleCasePK> {
    List<SampleCase> findAllBySampleCasePK_Pid(long pid);

    void deleteAllBySampleCasePK_Pid(long pid);
}
