package com.example.myweb;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.InitializingBean;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@ConfigurationProperties(prefix = "file")
public class FileStorageProperties implements InitializingBean {

    private String uploadDir;

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Path path = Paths.get(uploadDir);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }
}
