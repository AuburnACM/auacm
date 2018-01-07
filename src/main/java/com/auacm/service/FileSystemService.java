package com.auacm.service;


import com.google.gson.JsonObject;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileSystemService {
    Resource getProfilePicture(String username);

    boolean saveProfilePicture(String username, String data);

    Resource getProblemPdf(String shortName);

    boolean deleteProblem(String problemId);

    boolean saveProblemInputFile(String problemId, MultipartFile file, String outputName);

    boolean saveProblemOutputFile(String problemId, MultipartFile file, String outputName);

    boolean saveProblemInputZip(String problemId, MultipartFile file);

    boolean saveProblemOutputZip(String problemId, MultipartFile file);

    boolean saveProblemImportZip(String problemId, MultipartFile file);

    boolean saveSolutionFile(String problemId, MultipartFile file);

    boolean saveSolutionFile(String problemId, MultipartFile file, String outputName);

    boolean createProblemZip(String problemId, JsonObject problemData);

    Resource getProblemZip(String problemId);

    boolean unzipFile(String path);

    boolean saveTempFile(MultipartFile file, String outputPath, String outputName);

    String getFileContents(String path);

    String getTmpFolder();

    boolean doesFileExist(String path);

    boolean deleteFile(String path);

    boolean createFolder(String path);

    boolean moveFile(String path, String toPath);

    boolean copyFile(String path, String toPath);
}
