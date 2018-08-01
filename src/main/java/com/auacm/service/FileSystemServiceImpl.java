package com.auacm.service;

import com.auacm.api.model.JudgeFile;
import com.auacm.database.dao.FileSystemDao;
import com.auacm.database.model.Problem;
import com.auacm.exception.PdfNotFoundException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class FileSystemServiceImpl implements FileSystemService {
    private final String TEMP_FOLDER = "data/tmp/";
    private final String PROBLEM_FOLDER = "data/problems/";
    private final String SUBMISSION_FOLDER = "data/submits/";

    @Autowired
    private FileSystemDao fileSystemDao;

    @Autowired
    private ProblemService problemService;

    @Autowired
    private Gson gson;

    @Override
    public Resource getProfilePicture(String username) {
        if (fileSystemDao.fileExists(String.format("data/profile/%s.png", username))) {
            return fileSystemDao.getFile(String.format("data/profile/%s.png", username));
        } else {
            return fileSystemDao.getFile("data/profile/default/profile.png");
        }
    }

    @Override
    public void saveProfilePicture(String username, String data) {
        byte[] pictureData = Base64.getDecoder().decode(data);
        fileSystemDao.createDirectory("data/profile/");
        fileSystemDao.createFile(String.format("data/profile/%s.png", username), pictureData, true);
    }

    @Override
    public Resource getProblemPdf(String shortName) {
        Problem problem = problemService.getProblem(shortName);
        if (fileSystemDao.fileExists(String.format("data/problems/%d/info.pdf", problem.getPid()))) {
            return fileSystemDao.getFile(String.format("data/problems/%d/info.pdf", problem.getPid()));
        } else {
            throw new PdfNotFoundException("Failed to find the pdf for shortname " + shortName + ".");
        }
    }

    @Override
    public void deleteProblem(String id) {
        fileSystemDao.deleteFile(String.format("data/problems/%s", id));
    }

    @Override
    public boolean saveProblemInputFile(String problemId, MultipartFile file, String outputName) {
        String outputDir = String.format("data/problems/%s/in/", problemId);
        if (fileSystemDao.createDirectory(outputDir)) {
            fileSystemDao.saveFile(file, outputDir, outputName, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean saveProblemOutputFile(String problemId, MultipartFile file, String outputName) {
        String outputDir = String.format("data/problems/%s/out/", problemId);
        if (fileSystemDao.createDirectory(outputDir)) {
            fileSystemDao.saveFile(file, outputDir, outputName, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean saveProblemInputZip(String problemId, MultipartFile file) {
        String outputDir = String.format("data/problems/%s/", problemId);
        if (fileSystemDao.createDirectory(outputDir)) {
            fileSystemDao.saveFile(file, outputDir, true);
            fileSystemDao.unzip(outputDir + file.getOriginalFilename());
            fileSystemDao.deleteFile(outputDir + file.getOriginalFilename());
            return true;
        }
        return false;
    }

    @Override
    public boolean saveProblemOutputZip(String problemId, MultipartFile file) {
        String outputDir = String.format("data/problems/%s/", problemId);
        if (fileSystemDao.createDirectory(outputDir)) {
            fileSystemDao.saveFile(file, outputDir, true);
            fileSystemDao.unzip(outputDir + file.getOriginalFilename());
            fileSystemDao.deleteFile(outputDir + file.getOriginalFilename());
            return true;
        }
        return false;
    }

    @Override
    public boolean saveProblemImportZip(String problemId, MultipartFile file) {
        // TODO this?
        return false;
    }

    @Override
    public boolean saveSolutionFile(String problemId, MultipartFile file) {
        String outputDir = String.format("data/problems/%s/test/", problemId);
        if (fileSystemDao.createDirectory(outputDir)) {
            fileSystemDao.saveFile(file, outputDir, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean saveSolutionFile(String problemId, MultipartFile file, String outputName) {
        String outputDir = String.format("data/problems/%s/test/", problemId);
        if (fileSystemDao.createDirectory(outputDir)) {
            fileSystemDao.saveFile(file, outputDir, outputName, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean saveSubmissionFile(String submissionId, MultipartFile file) {
        String outputDir = String.format("data/submits/%s/", submissionId);
        if (fileSystemDao.createDirectory(outputDir)) {
            fileSystemDao.saveFile(file, outputDir, file.getOriginalFilename(), true);
            return true;
        }
        return false;
    }

    @Override
    public boolean createProblemZip(String problemId, JsonObject problemData) {
        problemData.add("exportVersion", new JsonPrimitive(1));
        fileSystemDao.createFile(String.format("data/problems/%s/data.json", problemId), gson.toJson(problemData), true);
        return fileSystemDao.createZip(String.format("data/problems/%s/export.zip", problemId),
                true, String.format("data/problems/%s/", problemId),
                String.format("data/problems/%s/in/", problemId), String.format("data/problems/%s/out/", problemId),
                String.format("data/problems/%s/test/", problemId),
                String.format("data/problems/%s/data.json", problemId));
    }

    @Override
    public Resource getProblemZip(String problemId) {
        return fileSystemDao.getFile(String.format("data/problems/%s/export.zip", problemId));
    }

    @Override
    public void unzipFile(String path) {
        fileSystemDao.unzip(path);
    }

    @Override
    public boolean saveTempFile(MultipartFile file, String outputPath, String outputName) {
        if (fileSystemDao.createDirectory(TEMP_FOLDER) && fileSystemDao.createDirectory(TEMP_FOLDER + outputPath)) {
            fileSystemDao.saveFile(file, TEMP_FOLDER + outputPath, outputName, true);
            return true;
        }
        return false;
    }

    @Override
    public String getFileContents(String path) {
        return fileSystemDao.readFile(path);
    }

    @Override
    public String getTmpFolder() {
        return TEMP_FOLDER;
    }

    @Override
    public boolean doesFileExist(String path) {
        return fileSystemDao.fileExists(path);
    }

    @Override
    public void deleteFile(String path) {
        fileSystemDao.deleteFile(path);
    }

    @Override
    public boolean createFolder(String path) {
        return fileSystemDao.createDirectory(path);
    }

    @Override
    public void moveFile(String path, String toPath) {
        fileSystemDao.move(path, toPath);
    }

    @Override
    public void copyFile(String path, String toPath) {
        fileSystemDao.copy(path, toPath);
    }

    @Override
    public List<JudgeFile<String>> getInputFilesAsStrings(String problemId) {
        List<File> inputFileNames = fileSystemDao.listDirectory(String.format("%s%s/in/", PROBLEM_FOLDER, problemId));
        List<JudgeFile<String>> data = new ArrayList<>();
        for (File in : inputFileNames) {
            String tempData = fileSystemDao.readFile(String.format("%s%s/in/%s", PROBLEM_FOLDER, problemId, in.getName()));
            data.add(new JudgeFile<>(in.getName(), tempData));
        }
        return data;
    }

    @Override
    public List<JudgeFile<String>> getOutputFilesAsStrings(String problemId) {
        List<File> outputFileNames = fileSystemDao.listDirectory(String.format("%s%s/out/", PROBLEM_FOLDER, problemId));
        List<JudgeFile<String>> data = new ArrayList<>();
        for (File out : outputFileNames) {
            String tempData = fileSystemDao.readFile(String.format("%s%s/out/%s", PROBLEM_FOLDER, problemId, out.getName()));
            data.add(new JudgeFile<>(out.getName(), tempData));
        }
        return data;
    }

    @Override
    public List<JudgeFile<byte[]>> getInputFilesAsByteArrays(String problemId) {
        List<File> outputFileNames = fileSystemDao.listDirectory(String.format("%s%s/in/", PROBLEM_FOLDER, problemId));
        List<JudgeFile<byte[]>> data = new ArrayList<>();
        for (File out : outputFileNames) {
            byte[] tempData = fileSystemDao.readFileAsByteArray(String.format("%s%s/in/%s", PROBLEM_FOLDER, problemId, out.getName()));
            data.add(new JudgeFile<>(out.getName(), tempData));
        }
        return data;
    }

    @Override
    public List<JudgeFile<byte[]>> getOutputFilesAsByteArrays(String problemId) {
        List<File> outputFileNames = fileSystemDao.listDirectory(String.format("%s%s/out/", PROBLEM_FOLDER, problemId));
        List<JudgeFile<byte[]>> data = new ArrayList<>();
        for (File out : outputFileNames) {
            byte[] tempData = fileSystemDao.readFileAsByteArray(String.format("%s%s/out/%s", PROBLEM_FOLDER, problemId, out.getName()));
            data.add(new JudgeFile<>(out.getName(), tempData));
        }
        return data;
    }

    @Override
    public String getSolutionFileAsString(String problemId) {
        // This isn't even possible
        return null;
    }

    @Override
    public byte[] getSolutionFileAsByteArray(String problemId) {
        // Not possible right now
        return new byte[0];
    }

    @Override
    public JudgeFile<byte[]> getSubmissionFileAsByteArray(String submissionId, String fileName) {
        return new JudgeFile<>(fileName, fileSystemDao.readFileAsByteArray(String.format("%s%s/%s", SUBMISSION_FOLDER,
                submissionId, fileName)));
    }

    @Override
    public JudgeFile<String> getSubmissionFileAsString(String submissionId, String fileName) {
        return new JudgeFile<>(fileName, fileSystemDao.readFile(String.format("%s%s/%s", SUBMISSION_FOLDER,
                submissionId, fileName)));
    }
}
