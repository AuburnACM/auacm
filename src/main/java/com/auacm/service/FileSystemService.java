package com.auacm.service;


import com.auacm.api.model.JudgeFile;
import com.google.gson.JsonObject;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileSystemService {
    Resource getProfilePicture(String username);

    void saveProfilePicture(String username, String data);

    Resource getProblemPdf(String shortName);

    void deleteProblem(String problemId);

    boolean saveProblemInputFile(String problemId, MultipartFile file, String outputName);

    boolean saveProblemOutputFile(String problemId, MultipartFile file, String outputName);

    boolean saveProblemInputZip(String problemId, MultipartFile file);

    boolean saveProblemOutputZip(String problemId, MultipartFile file);

    boolean saveProblemImportZip(String problemId, MultipartFile file);

    boolean saveSolutionFile(String problemId, MultipartFile file);

    boolean saveSolutionFile(String problemId, MultipartFile file, String outputName);

    boolean saveSubmissionFile(String submissionId, MultipartFile file);

    boolean createProblemZip(String problemId, JsonObject problemData);

    Resource getProblemZip(String problemId);

    void unzipFile(String path);

    boolean saveTempFile(MultipartFile file, String outputPath, String outputName);

    String getFileContents(String path);

    String getTmpFolder();

    boolean doesFileExist(String path);

    void deleteFile(String path);

    boolean createFolder(String path);

    void moveFile(String path, String toPath);

    void copyFile(String path, String toPath);

    List<JudgeFile<String>> getInputFilesAsStrings(String problemId);

    List<JudgeFile<String>> getOutputFilesAsStrings(String problemId);

    List<JudgeFile<byte[]>> getInputFilesAsByteArrays(String problemId);

    List<JudgeFile<byte[]>> getOutputFilesAsByteArrays(String problemId);

    String getSolutionFileAsString(String problemId);

    byte[] getSolutionFileAsByteArray(String problemId);

    JudgeFile<String> getSubmissionFileAsString(String submissionId, String fileName);

    JudgeFile<byte[]> getSubmissionFileAsByteArray(String submissionId, String fileName);
}
