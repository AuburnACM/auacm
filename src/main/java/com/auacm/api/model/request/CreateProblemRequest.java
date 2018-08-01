package com.auacm.api.model.request;

import com.auacm.api.model.SampleCase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class CreateProblemRequest {
    private String appearedIn;
    private String description;
    private String difficulty;
    private String importZipType;
    private String inputDescription;
    private String name;
    private String outputDescription;
    private String sampleCases;
    @JsonIgnore
    private List<SampleCase> sampleCaseList;
    private Long timeLimit;

    private MultipartFile importZip;
    private MultipartFile inputZip;
    private MultipartFile outputZip;
    private MultipartFile solutionFile;

    private List<MultipartFile> inputFiles;
    private List<MultipartFile> outputFiles;

    @JsonIgnore
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public List<SampleCase> getSampleCaseList() {
        if (sampleCaseList == null) {
            sampleCaseList = new ArrayList<>();
            JsonArray jsonArray = new JsonParser().parse(sampleCases).getAsJsonArray();
            jsonArray.forEach(e -> sampleCaseList.add(gson.fromJson(e, SampleCase.class)));
        }
        return sampleCaseList;
    }
}
