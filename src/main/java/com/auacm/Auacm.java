package com.auacm;

import com.auacm.util.FileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;

import java.io.File;

@SpringBootApplication(scanBasePackages = "com.auacm")
public class Auacm {
    private Logger logger;

    public Auacm() {
        logger = LoggerFactory.getLogger("AUACM");
        logger.info("Starting Auacm...");
    }

    public static void main(String args[]) {
        initializeConfig();
        SpringApplication.run(Auacm.class, args);
    }

    private static void initializeConfig() {
        File configFolder = new File("config/");
        FileUtils.copyFileFromJar("config/application.properties", "application.properties", configFolder);
        FileUtils.copyFolderFromJar("BOOT-INF/classes/public/", "public", true);
        FileUtils.copyFolderFromJar("BOOT-INF/classes/data/", "data", true);
    }

//    @Bean
//    ProtobufHttpMessageConverter protobufHttpMessageConverter() {
//        return new ProtobufHttpMessageConverter();
//    }

    @Bean
    Gson gson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }
}
