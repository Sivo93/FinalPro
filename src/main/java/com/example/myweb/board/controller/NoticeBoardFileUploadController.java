package com.example.myweb.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.myweb.board.service.NoticeBoardService;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class NoticeBoardFileUploadController {

//    private static final String UPLOAD_DIR = "src/main/resources/static/upload/";
    
    private final NoticeBoardService noticeBoardService;

    @PostMapping("/uploadImageNoticeBoard")
    @ResponseBody
    public Map<String, Object> handleFileUpload(@RequestParam("upload") MultipartFile file) {
        Map<String, Object> responseData = new HashMap<>();

        if (file.isEmpty()) {
            responseData.put("uploaded", false);
            responseData.put("error", "파일이 업로드되지 않았습니다.");
            return responseData;
        }

        try {
            String fileUrl = noticeBoardService.saveFile(file);
            responseData.put("uploaded", true);
            responseData.put("url", fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            responseData.put("uploaded", false);
            responseData.put("error", "파일 업로드 실패");
        }

        return responseData;
    }

}
