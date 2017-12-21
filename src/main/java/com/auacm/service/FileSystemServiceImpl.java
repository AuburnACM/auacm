package com.auacm.service;

import com.auacm.database.dao.FileSystemDao;
import com.auacm.database.model.Problem;
import com.auacm.exception.PdfNotFoundException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemServiceImpl implements FileSystemService {
    private final String TEMP_FOLDER = "data/tmp/";
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
    public Resource getProblemPdf(String shortName) {
        Problem problem = problemService.getProblem(shortName);
        if (fileSystemDao.fileExists(String.format("data/problems/%d/info.pdf", problem.getPid()))) {
            return fileSystemDao.getFile(String.format("data/problems/%d/info.pdf", problem.getPid()));
        } else {
            throw new PdfNotFoundException("Failed to find the pdf for shortname " + shortName + ".");
        }
    }

    @Override
    public boolean deleteProblem(String id) {
        return fileSystemDao.deleteFile(String.format("data/problems/%s", id));
    }

    @Override
    public boolean saveProblemInputFile(String problemId, MultipartFile file, String outputName) {
        String outputDir = String.format("data/problems/%s/in/", problemId);
        if (fileSystemDao.createDirectory(outputDir)) {
            return fileSystemDao.saveFile(file, outputDir, outputName, true);
        }
        return false;
    }

    @Override
    public boolean saveProblemOutputFile(String problemId, MultipartFile file, String outputName) {
        String outputDir = String.format("data/problems/%s/out/", problemId);
        if (fileSystemDao.createDirectory(outputDir)) {
            return fileSystemDao.saveFile(file, outputDir, outputName, true);
        }
        return false;
    }

    @Override
    public boolean saveProblemInputZip(String problemId, MultipartFile file) {
        String outputDir = String.format("data/problems/%s/", problemId);
        if (fileSystemDao.createDirectory(outputDir)) {
            boolean success = true;
            success = fileSystemDao.saveFile(file, outputDir, true);
            if (success) {
                success = fileSystemDao.unzip(outputDir + file.getOriginalFilename());
            }
            if (success) {
                success = fileSystemDao.deleteFile(outputDir + file.getOriginalFilename());
            }
            return success;
        }
        return false;
    }

    @Override
    public boolean saveProblemOutputZip(String problemId, MultipartFile file) {
        String outputDir = String.format("data/problems/%s/", problemId);
        if (fileSystemDao.createDirectory(outputDir)) {
            boolean success = true;
            success = fileSystemDao.saveFile(file, outputDir, true);
            if (success) {
                success = fileSystemDao.unzip(outputDir + file.getOriginalFilename());
            }
            if (success) {
                success = fileSystemDao.deleteFile(outputDir + file.getOriginalFilename());
            }
            return success;
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
            return fileSystemDao.saveFile(file, outputDir, true);
        }
        return false;
    }

    @Override
    public boolean saveSolutionFile(String problemId, MultipartFile file, String outputName) {
        String outputDir = String.format("data/problems/%s/test/", problemId);
        if (fileSystemDao.createDirectory(outputDir)) {
            return fileSystemDao.saveFile(file, outputDir, outputName, true);
        }
        return false;
    }

    @Override
    public boolean createProblemZip(String problemId, JsonObject problemData) {
        problemData.add("exportVersion", new JsonPrimitive(1));
        if (fileSystemDao.createFile(String.format("data/problems/%s/data.json", problemId), gson.toJson(problemData), true)) {
            return fileSystemDao.createZip(String.format("data/problems/%s/export.zip", problemId),
                    true, String.format("data/problems/%s/", problemId),
                    String.format("data/problems/%s/in/", problemId), String.format("data/problems/%s/out/", problemId),
                    String.format("data/problems/%s/test/", problemId),
                    String.format("data/problems/%s/data.json", problemId));
        }
        return false;
    }

    @Override
    public Resource getProblemZip(String problemId) {
        return fileSystemDao.getFile(String.format("data/problems/%s/export.zip", problemId));
    }

    @Override
    public boolean unzipFile(String path) {
        return fileSystemDao.unzip(path);
    }

    @Override
    public boolean saveTempFile(MultipartFile file, String outputPath, String outputName) {
        if (fileSystemDao.createDirectory(TEMP_FOLDER) && fileSystemDao.createDirectory(TEMP_FOLDER + outputPath)) {
            return fileSystemDao.saveFile(file, TEMP_FOLDER + outputPath, outputName, true);
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
    public boolean deleteFile(String path) {
        return fileSystemDao.deleteFile(path);
    }

    @Override
    public boolean createFolder(String path) {
        return fileSystemDao.createDirectory(path);
    }

    @Override
    public boolean moveFile(String path, String toPath) {
        return fileSystemDao.move(path, toPath);
    }

    @Override
    public boolean copyFile(String path, String toPath) {
        return fileSystemDao.copy(path, toPath);
    }
}
