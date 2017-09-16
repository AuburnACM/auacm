package io.github.auburnacm.auacm.util;

import io.github.auburnacm.auacm.Auacm;

import java.io.*;

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
