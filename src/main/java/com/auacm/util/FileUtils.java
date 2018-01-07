package com.auacm.util;

import com.auacm.Auacm;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Component
public class FileUtils {
    /**
     * Extracts a file from the jar and places it in the given parent directory.
     *
     * @param fileName - the name of the file to extract from the jar
     * @param parentDirectory - the parent directory to place the file in
     */
    public static void copyFileFromJar(String jarName, String fileName, File parentDirectory) {
        File config = new File(parentDirectory, fileName);
        if (!parentDirectory.exists()) {
            parentDirectory.mkdirs();
        }
        if (!config.exists()) {
            try {
                InputStream stream = Auacm.class.getClassLoader().getResourceAsStream(jarName);
                FileOutputStream out = new FileOutputStream(config, false);
                byte[] buffer = new byte[4096];
                int done;
                while ((done = stream.read(buffer)) > 0) {
                    out.write(buffer, 0, done);
                }
                out.close();
            } catch (IOException e) {
                System.out.println("Failed to create " + fileName + "!");
            }
        }
    }

    public static void copyFolderFromJar(String jarFolderName, String outputDirName, boolean replace) {
        File outputDir = new File(jarFolderName);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        try {
            String path = Auacm.class.getProtectionDomain().getCodeSource().getLocation().getPath();
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
            e.printStackTrace();
        }
    }

    public static void copyFileFromJar(String jarName, String outputFileName, String outputFolder, boolean replace) {
        File file = new File(outputFolder, outputFileName);
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        if (file.exists() && replace) {
            file.delete();
        }
        try {
            InputStream stream = Auacm.class.getClassLoader().getResourceAsStream(jarName);
            FileOutputStream out = new FileOutputStream(file, false);
            byte[] buffer = new byte[4096];
            int done;
            while ((done = stream.read(buffer)) > 0) {
                out.write(buffer, 0, done);
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Failed to create " + outputFileName + "!");
        }
    }

    public String readFile(String fileName, File parentDirectory) {
        File file = new File(parentDirectory, fileName);
        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                StringBuilder data = new StringBuilder();
                while (reader.ready()) {
                    data.append(reader.readLine()).append("\n");
                }
                return data.toString();
            } catch (IOException e) {
                throw new RuntimeException("Failed to read file: " + file.getAbsolutePath());
            }
        } else {
            throw new RuntimeException("Failed to open file: " + file.getAbsolutePath());
        }
    }

    public String readFile(String path) {
        File file = new File(path);
        return readFile(file.getName(), file.getParentFile());
    }

    public boolean deleteFile(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                boolean success = true;
                File[] children = file.listFiles();
                if (children != null) {
                    for (File child : children) {
                        success = success && deleteFile(child);
                    }
                }
                return success && file.delete();
            } else {
                return file.delete();
            }
        } else {
            return true;
        }
    }

    public boolean saveFile(MultipartFile file, String outputDir, String outputName, boolean overwrite) {
        if (file.isEmpty()) {
            return false;
        } else {
            try {
                InputStream in = file.getInputStream();
                FileOutputStream out = new FileOutputStream(new File(outputDir, outputName), !overwrite);
                int size = 0;
                byte[] buffer = new byte[2048];
                while ((size = in.read(buffer)) > 0) {
                    out.write(buffer, 0, size);
                }
                out.close();
                in.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }

    public boolean saveFile(File file, String outputDir, String outputName, boolean overwrite) {
        if (!file.exists()) {
            return false;
        } else {
            try {
                FileInputStream in = new FileInputStream(file);
                FileOutputStream out = new FileOutputStream(new File(outputDir, outputName), !overwrite);
                int size = 0;
                byte[] buffer = new byte[2048];
                while ((size = in.read(buffer)) > 0) {
                    out.write(buffer, 0, size);
                }
                out.close();
                in.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }

    public boolean saveFile(String path, String data, boolean overwrite) {
        try {
            FileOutputStream out = new FileOutputStream(new File(path), !overwrite);
            out.write(data.getBytes("utf8"));
            out.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean saveFile(String path, byte[] data, boolean overwrite) {
        try {
            FileOutputStream out = new FileOutputStream(new File(path), !overwrite);
            out.write(data);
            out.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean saveInputStream(InputStream inputStream, String outputDir, String outputName, boolean overwrite) {
        try {
            FileOutputStream out;
            if (outputDir == null) {
                File temp = new File(outputName);
                if (!temp.getParentFile().exists()) {
                    temp.getParentFile().mkdirs();
                }
                out = new FileOutputStream(temp, !overwrite);
            } else {
                File temp = new File(outputDir, outputName);
                if (!temp.getParentFile().exists()) {
                    temp.getParentFile().mkdirs();
                }
                out = new FileOutputStream(temp, !overwrite);
            }
            int size = 0;
            byte[] buffer = new byte[2048];
            while ((size = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, size);
            }
            out.close();
            inputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean unzipFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            try {
                ZipFile zipFile = new ZipFile(file);
                Enumeration<? extends ZipEntry> zipEnum = zipFile.entries();
                boolean success = true;
                while (zipEnum.hasMoreElements() && success) {
                    ZipEntry current = zipEnum.nextElement();
                    if (current.isDirectory()) {
                        success = new File(file.getParentFile().getAbsolutePath() + "/" + current.getName()).mkdirs();
                    } else {
                        success = saveInputStream(zipFile.getInputStream(current), null, file.getParentFile().getAbsolutePath() + "/" + current.getName(), true);
                    }
                }
                return success;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public List<String> getFileNames(String path) {
        File file = new File(path);
        ArrayList<String> list = new ArrayList<>();
        if (file.exists()) {
            if (file.isDirectory()) {
                String[] files = file.list();
                if (files != null) {
                    for (String f : files) {
                        File temp = new File(f);
                        if (temp.isDirectory()) {
                            List<String> tempList = getFileNames(path + temp.getName());
                            list.addAll(tempList);
                        } else {
                            list.add(path + temp.getName());
                        }
                    }
                }
            } else {
                list.add(path);
            }
        }
        return list;
    }

    public boolean copyFileToPath(String path, String toPath) {
        try {
            File newFile = new File(toPath);
            File oldFile = new File(toPath);
            if (oldFile.exists()) {
                if (!deleteFile(oldFile)) {
                    return false;
                }
            }
            if (!newFile.getParentFile().exists()) {
                if (!newFile.getParentFile().mkdirs()) {
                    return false;
                }
            }
            Files.copy(new File(path).toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean moveFileToPath(String path, String toPath) {
        try {
            File newFile = new File(toPath);
            File oldFile = new File(toPath);
            if (oldFile.exists()) {
                if (!deleteFile(oldFile)) {
                    return false;
                }
            }
            if (!newFile.getParentFile().exists()) {
                if (!newFile.getParentFile().mkdirs()) {
                    return false;
                }
            }
            Files.move(new File(path).toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
