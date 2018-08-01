package com.auacm.database.dao;

import com.auacm.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Repository
public class FileSystemDaoImpl implements FileSystemDao {
    @Autowired
    private FileUtils fileUtils;

    @Override
    public Resource getFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            return new FileSystemResource(path);
        }
        return null;
    }

    @Override
    public void saveFile(MultipartFile file, String path, boolean overwrite) {
        fileUtils.saveFile(file, path, file.getOriginalFilename(), overwrite);
    }

    @Override
    public void saveFile(MultipartFile file, String path, String outputName, boolean overwrite) {
        fileUtils.saveFile(file, path, outputName, overwrite);
    }

    @Override
    public boolean fileExists(String path) {
        return new File(path).exists();
    }

    @Override
    public boolean createDirectory(String path) {
        File file = new File(path);
        if (file.exists()) {
            return true;
        } else {
            return file.mkdirs();
        }
    }

    @Override
    public void deleteFile(String path) {
        fileUtils.deleteFile(path);
    }

    @Override
    public void unzip(String zipPath) {
        fileUtils.unzipFile(zipPath);
    }

    @Override
    public void createFile(String path, String data, boolean overwrite) {
        fileUtils.saveFile(path, data, overwrite);
    }

    @Override
    public void createFile(String path, byte[] data, boolean overwrite) {
        fileUtils.saveFile(path, data, overwrite);
    }

    @Override
    public boolean createZip(String path, boolean overwrite, String pathReplace, String... files) {
        try {
            ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(path));
            for (String f : files) {
                Collection<String> fileNames = fileUtils.getFileNames(f);
                for (String file : fileNames) {
                    outputStream.putNextEntry(new ZipEntry(file.replace(pathReplace, "")));
                    FileInputStream in = new FileInputStream(new File(file));

                    int size = 0;
                    byte[] buff = new byte[2048];
                    while ((size = in.read(buff)) >= 0) {
                        outputStream.write(buff, 0, size);
                    }
                    in.close();
                    outputStream.closeEntry();
                }
            }
            outputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String readFile(String path) {
        try {
            return fileUtils.readFile(path);
        } catch (RuntimeException e) {
            return null;
        }
    }

    @Override
    public byte[] readFileAsByteArray(String path) {
        return fileUtils.readFileAsByteArray(path);
    }

    @Override
    public void move(String path, String toPath) {
        fileUtils.moveFile(path, toPath);
    }

    @Override
    public void copy(String path, String toPath) {
        fileUtils.copyFile(path, toPath);
    }

    @Override
    public List<File> listDirectory(String path) {
        return new ArrayList<>(org.apache.commons.io.FileUtils.listFiles(new File(path), null, true));
    }
}
