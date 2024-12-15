package com.example.myweb.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.myweb.user.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    // 사용자가 로그인했는지 확인하고, 환영 페이지로 리디렉션
    @GetMapping("/welcome")
    public String welcome(Model model, HttpSession session) {
        String nickname = (String) session.getAttribute("nickname");
        if (nickname == null) {
            return "redirect:/user/login"; // 로그인 페이지로 리디렉션
        }
        model.addAttribute("nickname", nickname); // 모델에 닉네임 저장
        model.addAttribute("users", userRepository.findAll()); // 모든 사용자 목록을 모델에 추가
        return "welcome"; // welcome.html 페이지로 이동
    }

}
