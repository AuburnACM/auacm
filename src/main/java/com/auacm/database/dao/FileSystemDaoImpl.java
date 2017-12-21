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
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
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
    public boolean saveFile(MultipartFile file, String path, boolean overwrite) {
        return fileUtils.saveFile(file, path, file.getOriginalFilename(), overwrite);
    }

    @Override
    public boolean saveFile(MultipartFile file, String path, String outputName, boolean overwrite) {
        return fileUtils.saveFile(file, path, outputName, overwrite);
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
    public boolean deleteFile(String path) {
        return fileUtils.deleteFile(new File(path));
    }

    @Override
    public boolean unzip(String zipPath) {
        return fileUtils.unzipFile(zipPath);
    }

    @Override
    public boolean createFile(String path, String data, boolean overwrite) {
        return fileUtils.saveFile(path, data, overwrite);
    }

    @Override
    public boolean createZip(String path, boolean overwrite, String pathReplace, String... files) {
        try {
            ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(path));
            for (String f : files) {
                List<String> fileNames = fileUtils.getFileNames(f);
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
    public boolean move(String path, String toPath) {
        return fileUtils.moveFileToPath(path, toPath);
    }

    @Override
    public boolean copy(String path, String toPath) {
        return fileUtils.copyFileToPath(path, toPath);
    }
}
