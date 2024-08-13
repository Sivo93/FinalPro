package com.example.myweb.chat.controller;

import com.example.myweb.FileStorageProperties;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/downloadFile")
public class FileDownloadController {
	
	
    private final FileStorageProperties fileStorageProperties;

    public FileDownloadController(FileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;
    }
    
    
    // 파일 다운로드 메서드
    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
        	// 파일 경로 설정, 존재하는지
            Path filePath = Paths.get(fileStorageProperties.getUploadDir()).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            // 리소스가 존재하면 파일 다운로드할 수 있도록 responseentity 생성
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
            	// 파일이 존재하지 않는 경우 404
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
        	// 예외 발생
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
