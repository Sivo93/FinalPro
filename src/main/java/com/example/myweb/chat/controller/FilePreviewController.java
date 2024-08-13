package com.example.myweb.chat.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FilePreviewController {

    @Value("${file.upload-dir}")
    private String uploadDir;
    
    // 파일 미리보기
    @GetMapping("/chat/filePreview")
    public String previewFile(@RequestParam("fileName") String fileName, Model model) {
        String fileUrl = "/downloadFile/" + fileName;
        String fileType = getFileType(fileName);

        model.addAttribute("fileUrl", fileUrl);
        model.addAttribute("fileType", fileType);
        model.addAttribute("fileName", fileName);

        return "chat/filePreview";
    }
    
    // 파일 확장자를 기반으로파일 유형 반환
    private String getFileType(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        if (fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png") || fileExtension.equals("gif")) {
            return "image";
        } else if (fileExtension.equals("pdf")) {
            return "pdf";
        } else if (fileExtension.equals("ppt") || fileExtension.equals("pptx")) {
            return "ppt";
        } else {
            return "unsupported";
        }
    }
}
