package com.example.myweb.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.myweb.board.service.FreeBoardService;

import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class BoardFileUploadController {

//    private static final String UPLOAD_DIR = "src/main/resources/static/upload/";
    
    private final FreeBoardService freeBoardService;

    @PostMapping("/uploadImage")
    @ResponseBody
    public Map<String, Object> handleFileUpload(@RequestParam("upload") MultipartFile file) {
        Map<String, Object> responseData = new HashMap<>();

        if (file.isEmpty()) {
            responseData.put("uploaded", false);
            responseData.put("error", "파일이 업로드되지 않았습니다.");
            return responseData;
        }

        try {
            String fileUrl = freeBoardService.saveFile(file);
            responseData.put("uploaded", true);
            responseData.put("url", fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            responseData.put("uploaded", false);
            responseData.put("error", "파일 업로드 실패");
        }

        return responseData;
    }
    
//    @PostMapping("/uploadImage")
//    @ResponseBody
//    public String handleFileUpload(@RequestParam("upload") MultipartFile file, RedirectAttributes redirectAttributes) {
//        if (file.isEmpty()) {
//            return "파일이 업로드되지 않았습니다.";
//        }
//
//        try {
//            // 파일 저장 및 FileEntity 저장
//            String fileUrl = freeBoardService.saveFile(file);
//
//            // CKEditor에 반환할 JSON 데이터
//            return "{\"uploaded\": true, \"url\": \"" + fileUrl + "\"}";
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "{\"uploaded\": false, \"error\": {\"message\": \"파일 업로드 실패\"}}";
//        }
//    }
}
