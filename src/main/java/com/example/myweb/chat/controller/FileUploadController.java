package com.example.myweb.chat.controller;

import com.example.myweb.ChatFileStorageProperties;
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

    private final ChatFileStorageProperties chatFileStorageProperties;

    public FileUploadController(ChatFileStorageProperties chatFileStorageProperties) {
        this.chatFileStorageProperties = chatFileStorageProperties;
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> uploadChatFile(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, chatFileStorageProperties.getUploadDir());
    }

    private ResponseEntity<Map<String, String>> uploadFile(MultipartFile file, String uploadDir) {
        Map<String, String> response = new HashMap<>();
        try {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = StringUtils.getFilenameExtension(fileName);
            String storedFileName = UUID.randomUUID().toString() + "." + fileExtension;
            Path targetLocation = Paths.get(chatFileStorageProperties.getUploadDir()).resolve(storedFileName);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

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
