package com.auacm.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SampleCase {
    private Long caseNum;
    private String input;
    private String output;

    public SampleCase(com.auacm.database.model.SampleCase sampleCase) {
        this.caseNum = sampleCase.getSampleCasePK().getCaseNum();
        this.input = sampleCase.getInput();
        this.output = sampleCase.getOutput();
    }
}
