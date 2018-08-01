package com.auacm.util;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Slf4j
@Component
public class FileUtilsImpl implements FileUtils {
    public void copyFileFromJar(String jarName, String outputFileName, String outputDir) {
        copyFileFromJar(jarName, outputFileName, outputDir, false);
    }

    public void copyFileFromJar(String jarName, String outputFileName, String outputFolder, boolean replace) {
        File parentDir = new File(outputFolder);
        File file = new File(parentDir, outputFileName);
        try {
            @Cleanup InputStream stream = FileUtilsImpl.class.getClassLoader().getResourceAsStream(jarName);
            if (!file.exists() || file.exists() && replace) {
                org.apache.commons.io.FileUtils.copyInputStreamToFile(stream, file);
            }
        } catch (IOException e) {
            log.error("Failed to create " + outputFileName + "!", e);
        }
    }

    public void copyFolderFromJar(String jarFolderName, String outputDirName, boolean replace) {
        File outputDir = new File(jarFolderName);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        try {
            String path = FileUtilsImpl.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            String finalPath = path.substring(path.indexOf(":") + 1, path.indexOf("!"));
            JarFile file = new JarFile(finalPath);
            Enumeration<JarEntry> entryEnumeration = file.entries();
            while (entryEnumeration.hasMoreElements()) {
                JarEntry current = entryEnumeration.nextElement();
                if (current.getName().startsWith(jarFolderName) && !current.getName().endsWith("/")) {
                    copyFileFromJar(current.getName(), current.getName().replace(jarFolderName, ""), outputDirName, replace);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public String readFile(String fileName, String parentDir) {
        File file = new File(parentDir, fileName);
        try {
            return org.apache.commons.io.FileUtils.readFileToString(file, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + file.getAbsolutePath());
        }
    }

    @Override
    public String readFile(String fileName) {
        File file = new File(fileName);
        return readFile(file.getName(), file.getParent());
    }

    @Override
    public byte[] readFileAsByteArray(String fileName) {
        File file = new File(fileName);
        try {
            return org.apache.commons.io.FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Failed to read file!");
        }
    }

    @Override
    public void deleteFile(String fileName) {
        File file = new File(fileName);
        try {
            if (file.isDirectory()) {
                org.apache.commons.io.FileUtils.deleteDirectory(file);
            } else {
                org.apache.commons.io.FileUtils.deleteQuietly(file);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Failed to delete file!");
        }
    }

    @Override
    public void saveFile(MultipartFile file, String outputDir, String outputName, boolean overwrite) {
        File newFile = new File(outputDir, outputName);
        try {
            if (!newFile.exists() || overwrite)
            org.apache.commons.io.FileUtils.copyInputStreamToFile(file.getInputStream(), newFile);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Failed to save input stream!");
        }
    }

    @Override
    public void saveFile(File file, String outputDir, String outputName, boolean overwrite) {
        File outputFile = new File(outputDir, outputName);
        try {
            org.apache.commons.io.FileUtils.copyFile(file, outputFile, true);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Failed to save file!");
        }
    }

    @Override
    public void saveFile(String path, String data, boolean overwrite) {
        try {
            saveFile(path, data.getBytes("UTF-8"), overwrite);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Failed to save file!");
        }
    }

    @Override
    public void saveFile(String path, byte[] data, boolean overwrite) {
        File file = new File(path);
        try {
            if (!file.exists() || overwrite) {
                org.apache.commons.io.FileUtils.copyToFile(new ByteArrayInputStream(data), file);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Failed to save file!");
        }
    }

    @Override
    public void saveInputStream(InputStream inputStream, String outputDir, String outputName, boolean overwrite) {
        File file;
        if (outputDir != null) {
            file = new File(outputDir, outputName);
        } else {
            file = new File(outputName);
        }
        try {
            if (!file.exists() || overwrite) {
                org.apache.commons.io.FileUtils.copyInputStreamToFile(inputStream, file);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Failed to save Input Stream!");
        }
    }

    public void unzipFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            try {
                @Cleanup ZipFile zipFile = new ZipFile(file);
                Enumeration<? extends ZipEntry> zipEnum = zipFile.entries();
                boolean success = true;
                while (zipEnum.hasMoreElements() && success) {
                    ZipEntry current = zipEnum.nextElement();
                    if (current.isDirectory()) {
                        success = new File(file.getParentFile().getAbsolutePath() + "/" + current.getName()).mkdirs();
                    } else {
                        saveInputStream(zipFile.getInputStream(current), null, file.getParentFile().getAbsolutePath() + "/" + current.getName(), true);
                    }
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException("Failed to unzip file!");
            }
        }
    }

    @Override
    public Collection<String> getFileNames(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            return org.apache.commons.io.FileUtils.listFiles(file, null, true)
                    .stream().map((file1 -> path + file1.getName())).collect(Collectors.toList());
        } else {
            return Collections.singletonList(path);
        }
    }

    @Override
    public void copyFile(String from, String to) {
        File fromFile = new File(from);
        File toFile = new File(to);
        try {
            org.apache.commons.io.FileUtils.copyFile(fromFile, toFile, true);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Failed to copy file!");
        }
    }

    @Override
    public void moveFile(String from, String to) {
        File fromFile = new File(from);
        File toFile = new File(to);
        try {
            if (fromFile.isDirectory()) {
                org.apache.commons.io.FileUtils.moveDirectory(fromFile, toFile);
            } else {
                org.apache.commons.io.FileUtils.moveFile(fromFile, toFile);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Failed to move file!");
        }
    }
}
