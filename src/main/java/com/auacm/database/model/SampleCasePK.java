package com.auacm.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class SampleCasePK implements Serializable {
    @JsonIgnore
    private Long pid;

    @Column(name = "case_num")
    private Long caseNum;

    public SampleCasePK() {}

    public SampleCasePK(Long pid) {
        this.pid = pid;
    }

    public SampleCasePK(Long pid, Long caseNum) {
        this.pid = pid;
        this.caseNum = caseNum;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Long getCaseNum() {
        return caseNum;
    }

    public void setCaseNum(Long caseNum) {
        this.caseNum = caseNum;
    }
}
