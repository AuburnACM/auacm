package com.auacm.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class CreateProblem {
    private String name;

    private String description;

    private String inputDesc;

    private String outputDesc;

    private String sampleCases;

    @JsonIgnore
    private List<SampleCase> sampleCaseList;

    private Long timeLimit;

    private Integer difficulty;

    private String appearedIn;

    private String importZipType;

    private MultipartFile importZip;

    private MultipartFile inputZip;

    private MultipartFile outputZip;

    private MultipartFile solutionFile;

    private List<MultipartFile> inputFiles;

    private List<MultipartFile> outputFiles;

    @JsonIgnore
    private Gson gson;

    public CreateProblem() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setInputDesc(String inputDesc) {
        this.inputDesc = inputDesc;
    }

    public String getOutputDesc() {
        return outputDesc;
    }

    public void setOutputDesc(String outputDesc) {
        this.outputDesc = outputDesc;
    }

    public String getSampleCases() {
        return sampleCases;
    }

    public void setSampleCases(String sampleCases) {
        this.sampleCases = sampleCases;
    }

    @JsonIgnore
    public List<SampleCase> getSampleCaseList() {
        if (sampleCaseList == null) {
            if (sampleCases == null) {
                return null;
            }
            JsonArray array = new JsonParser().parse(sampleCases).getAsJsonArray();
            ArrayList<SampleCase> list = new ArrayList<>();
            for (JsonElement e : array) {
                list.add(gson.fromJson(e, SampleCase.class));
            }
            return list;
        } else {
            return sampleCaseList;
        }
    }

    @JsonIgnore
    public void setSampleCaseList(List<SampleCase> sampleCases) {
        this.sampleCaseList = sampleCases;
    }

    public Long getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public String getAppearedIn() {
        return appearedIn;
    }

    public void setAppearedIn(String appearedIn) {
        this.appearedIn = appearedIn;
    }

    public String getImportZipType() {
        return importZipType;
    }

    public void setImportZipType(String importZipType) {
        this.importZipType = importZipType;
    }

    public MultipartFile getImportZip() {
        return importZip;
    }

    public void setImportZip(MultipartFile importZip) {
        this.importZip = importZip;
    }

    public MultipartFile getInputZip() {
        return inputZip;
    }

    public void setInputZip(MultipartFile inputZip) {
        this.inputZip = inputZip;
    }

    public MultipartFile getOutputZip() {
        return outputZip;
    }

    public void setOutputZip(MultipartFile outputZip) {
        this.outputZip = outputZip;
    }

    public MultipartFile getSolutionFile() {
        return solutionFile;
    }

    public void setSolutionFile(MultipartFile solutionFile) {
        this.solutionFile = solutionFile;
    }

    public List<MultipartFile> getInputFiles() {
        return inputFiles;
    }

    public void setInputFiles(List<MultipartFile> inputFiles) {
        this.inputFiles = inputFiles;
    }

    public List<MultipartFile> getOutputFiles() {
        return outputFiles;
    }

    public void setOutputFiles(List<MultipartFile> outputFiles) {
        this.outputFiles = outputFiles;
    }
}
