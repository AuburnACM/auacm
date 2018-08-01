package com.auacm.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

public interface FileUtils {
    void copyFileFromJar(String jarName, String outputFileName, String outputDir);

    void copyFileFromJar(String jarName, String outputFileName, String outputDir, boolean replace);

    void copyFolderFromJar(String jarName, String outputDirName, boolean replace);

    String readFile(String fileName);

    String readFile(String fileName, String parentDir);

    byte[] readFileAsByteArray(String fileName);

    void deleteFile(String fileName);

    void saveFile(MultipartFile file, String outputDir, String outputName, boolean overwrite);

    void saveFile(File file, String outputDir, String outputName, boolean overwrite);

    void saveFile(String fileName, String data, boolean overwrite);

    void saveFile(String path, byte[] data, boolean overwrite);

    void saveInputStream(InputStream inputStream, String outputDir, String outputName, boolean overwrite);

    void unzipFile(String fileName);

    Collection<String> getFileNames(String path);

    void copyFile(String from, String to);

    void moveFile(String from, String to);
}
