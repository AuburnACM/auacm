package com.auacm.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CompetitionProblemData {
    private String label;
    private String status;
    private Long submitCount;
    private Long submitTime;

    public CompetitionProblemData(String label) {
        this.label = label;
        this.status = "incorrect";
        this.submitCount = 0L;
        this.submitTime = 0L;
    }

    public void incrementSubmitCount() {
        submitCount++;
    }
}
