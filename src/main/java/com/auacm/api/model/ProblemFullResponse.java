package com.auacm.api.model;

import com.auacm.database.model.Problem;
import com.auacm.database.model.SampleCase;

import java.util.List;

public class ProblemFullResponse extends ProblemResponse {

    private List<SampleCase> sampleCases;

    private String description;

    private String inputDesc;

    private String outputDesc;

    public ProblemFullResponse() {

    }

    public ProblemFullResponse(Problem problem){
        super(problem);
        this.sampleCases = problem.getSampleCases();
        this.description = problem.getProblemData().getDescription();
        this.inputDesc = problem.getProblemData().getInputDescription();
        this.outputDesc = problem.getProblemData().getOutputDescription();
    }

    public List<SampleCase> getSampleCases() {
        return sampleCases;
    }

    public void setSample_cases(List<SampleCase> sampleCases) {
        this.sampleCases = sampleCases;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInputDesc() {
        return inputDesc;
    }

    public void setInputDesc(String input_desc) {
        this.inputDesc = input_desc;
    }

    public String getOutputDesc() {
        return outputDesc;
    }

    public void setOutputDesc(String output_desc) {
        this.outputDesc = output_desc;
    }
}
