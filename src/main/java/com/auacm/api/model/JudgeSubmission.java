package com.auacm.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class JudgeSubmission extends SubmissionStatus {
    private JudgeFile<byte[]> submissionFile;
    private JudgeFile<byte[]> solutionFile;
    private List<JudgeFile<byte[]>> inputData;
    private List<JudgeFile<byte[]>> outputData;
}
