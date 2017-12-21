package com.auacm.database.dao;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface FileSystemDao {
    Resource getFile(String path);

    boolean saveFile(MultipartFile file, String path, boolean overwrite);

    boolean saveFile(MultipartFile file, String directory, String outputName, boolean overwrite);

    boolean fileExists(String path);

    boolean createDirectory(String path);

    boolean deleteFile(String path);

    boolean unzip(String zipPath);

    boolean createFile(String path, String data, boolean overwrite);

    boolean createZip(String path, boolean overwrite, String pathReplace, String...files);

    String readFile(String path);

    boolean move(String path, String toPath);

    boolean copy(String path, String toPath);
}
