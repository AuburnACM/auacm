package io.github.auburnacm.auacm;

import io.github.auburnacm.auacm.util.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication(scanBasePackages = "io.github.auburnacm.auacm")
public class Auacm {
    public static void main(String args[]) {
        initializeConfig();
        SpringApplication.run(Auacm.class, args);
    }

    private static void initializeConfig() {
        File configFolder = new File("config/");
        FileUtils.copyFileFromJar("config/application.properties", "application.properties", configFolder);
        FileUtils.copyFolderFromJar("BOOT-INF/classes/public/", "public", true);
    }
}
