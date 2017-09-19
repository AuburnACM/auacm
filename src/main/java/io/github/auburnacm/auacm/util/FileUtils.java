package io.github.auburnacm.auacm.util;

import io.github.auburnacm.auacm.Auacm;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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

    public static String readFile(String fileName, File parentDirectory) {
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
}
