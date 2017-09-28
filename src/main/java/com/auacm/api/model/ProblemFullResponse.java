package com.auacm.api.model;

import com.auacm.database.model.Problem;
import com.auacm.database.model.SampleCase;

import java.util.List;

public class ProblemFullResponse extends ProblemResponse {

    private List<SampleCase> sample_cases;

    private String description;

    private String input_desc;

    private String output_desc;

    public ProblemFullResponse() {

    }

    public ProblemFullResponse(Problem problem){
        super(problem);
        this.sample_cases = problem.getSampleCases();
        this.description = problem.getProblemData().getDescription();
        this.input_desc = problem.getProblemData().getInputDescription();
        this.output_desc = problem.getProblemData().getOutputDescription();
    }

    public List<SampleCase> getSample_cases() {
        return sample_cases;
    }

    public void setSample_cases(List<SampleCase> sample_cases) {
        this.sample_cases = sample_cases;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInput_desc() {
        return input_desc;
    }

    public void setInput_desc(String input_desc) {
        this.input_desc = input_desc;
    }

    public String getOutput_desc() {
        return output_desc;
    }

    public void setOutput_desc(String output_desc) {
        this.output_desc = output_desc;
    }
}
