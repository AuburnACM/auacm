package com.auacm.model;

import com.auacm.api.model.request.CreateProblemRequest;
import com.auacm.request.MockRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

public class MockProblemBuilder {
    private CreateProblemRequest problem;

    public MockProblemBuilder() {
        problem = new CreateProblemRequest();
        problem.setName("Test Problem");
        problem.setDescription("Description for the problem.");
        problem.setDifficulty("50");
        problem.setInputDescription("Some input");
        problem.setOutputDescription("Some output");
        problem.setSampleCases(MockRequest.getProblemTestCases());
        problem.setTimeLimit(2L);
        ArrayList<MultipartFile> inputFiles = new ArrayList<>();
        inputFiles.add(new MockMultipartFile("inputFiles", "in1.txt",
                "text/plain", "Test\nLine2\n".getBytes()));
        inputFiles.add(new MockMultipartFile("inputFiles", "in2.txt",
                "text/plain", "Test2\nLine2\n".getBytes()));
        problem.setInputFiles(inputFiles);
        ArrayList<MultipartFile> outputFiles = new ArrayList<>();
        outputFiles.add(new MockMultipartFile("outputFiles", "out1.txt",
                "text/plain", "TestOutput1\nLine2\n".getBytes()));
        problem.setOutputFiles(outputFiles);
        problem.setSolutionFile(new MockMultipartFile("solutionFile", "Solution.java",
                "text/plain", "TestSolution\nLine2\n".getBytes()));
    }

    public MockProblemBuilder setName(String name) {
        this.problem.setName(name);
        return this;
    }

    public MockProblemBuilder setDescription(String description) {
        this.problem.setDescription(description);
        return this;
    }

    public MockProblemBuilder setDifficulty(int difficulty) {
        this.problem.setDifficulty(difficulty + "");
        return this;
    }

    public MockProblemBuilder setInputDescription(String inputDescription) {
        this.problem.setInputDescription(inputDescription);
        return this;
    }

    public MockProblemBuilder setOutputDescription(String outputDescription) {
        this.problem.setOutputDescription(outputDescription);
        return this;
    }

    public MockProblemBuilder setTimeLimit(long timeLimit) {
        this.problem.setTimeLimit(timeLimit);
        return this;
    }

    public MockProblemBuilder addInputFile(String name, String originalName, String contentType, String content) {
        this.problem.getInputFiles().add(new MockMultipartFile(name, originalName, contentType, content.getBytes()));
        return this;
    }

    public MockProblemBuilder addOutputFile(String name, String originalName, String contentType, String content) {
        this.problem.getOutputFiles().add(new MockMultipartFile(name, originalName, contentType, content.getBytes()));
        return this;
    }

    public MockProblemBuilder setSolutionFile(String name, String originalName, String contentType, String content) {
        this.problem.setSolutionFile(new MockMultipartFile(name, originalName, contentType, content.getBytes()));
        return this;
    }

    public CreateProblemRequest build() {
        return this.problem;
    }
}
