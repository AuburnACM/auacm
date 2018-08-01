package com.auacm.database.dao;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface FileSystemDao {
    Resource getFile(String path);

    void saveFile(MultipartFile file, String path, boolean overwrite);

    void saveFile(MultipartFile file, String directory, String outputName, boolean overwrite);

    boolean fileExists(String path);

    boolean createDirectory(String path);

    void deleteFile(String path);

    void unzip(String zipPath);

    void createFile(String path, String data, boolean overwrite);

    void createFile(String path, byte[] data, boolean overwrite);

    boolean createZip(String path, boolean overwrite, String pathReplace, String...files);

    String readFile(String path);

    byte[] readFileAsByteArray(String path);

    void move(String path, String toPath);

    void copy(String path, String toPath);

    List<File> listDirectory(String path);
}
