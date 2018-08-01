package com.auacm.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
public class SampleCasePK implements Serializable {
    @JsonIgnore
    private Long pid;

    @Column(name = "case_num")
    private Long caseNum;

    public SampleCasePK(Long pid) {
        this.pid = pid;
    }

    public SampleCasePK(Long pid, Long caseNum) {
        this.pid = pid;
        this.caseNum = caseNum;
    }

    public Long getCaseNum() {
        return caseNum;
    }
}
