package com.auacm;

import com.auacm.util.FileUtils;
import com.auacm.util.FileUtilsImpl;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

import java.io.File;

@SpringBootApplication
@EnableJpaRepositories
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
        FileUtils fileUtils = new FileUtilsImpl();
        fileUtils.copyFileFromJar("config/application.properties", "application.properties", "config/");
        fileUtils.copyFolderFromJar("BOOT-INF/classes/public/", "public", true);
        fileUtils.copyFolderFromJar("BOOT-INF/classes/data/", "data", true);
    }

//    @Bean
//    ProtobufHttpMessageConverter protobufHttpMessageConverter() {
//        return new ProtobufHttpMessageConverter();
//    }

    @Bean
    Gson gson() {
        return new GsonBuilder().setPrettyPrinting()
                .addSerializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                        return fieldAttributes.getAnnotation(JsonIgnore.class) != null;
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> aClass) {
                        return false;
                    }
                }).create();
    }
}
