package com.example.myweb.chat.controller;

import com.example.myweb.FileStorageProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/uploadFile")
public class FileUploadController {

    private final FileStorageProperties fileStorageProperties;

    public FileUploadController(FileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;
    }
    
    // 파일 업로드
    @PostMapping
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        try {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename()); //원래 파일이름을 가져와 경로 요소 제서
            String fileExtension = StringUtils.getFilenameExtension(fileName); // 확장자 추출
            String storedFileName = UUID.randomUUID().toString() + "." + fileExtension; // 파일 이름 생성
            Path targetLocation = Paths.get(fileStorageProperties.getUploadDir()).resolve(storedFileName); // 파일 저장 경로 설정

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING); // 파일 저장

            String fileDownloadUri = "/downloadFile/" + storedFileName;
            response.put("fileName", storedFileName);
            response.put("fileDownloadUri", fileDownloadUri);

            return ResponseEntity.ok(response);
        } catch (IOException ex) {
            response.put("error", "Could not store file " + file.getOriginalFilename() + ". Please try again!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception ex) {
            response.put("error", "Unexpected error occurred: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
