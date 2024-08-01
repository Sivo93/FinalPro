package com.example.myweb.user.controller;

import com.example.myweb.user.entity.UserEntity;
import com.example.myweb.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/signup")
    public String signup() {
        return "user/save";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam("loginid") String loginid, @RequestParam("email") String email, @RequestParam("pw") String pw, @RequestParam("signupType") String signupType, @RequestParam("nickname") String nickname, @RequestParam("name") String name, @RequestParam("address") String address, @RequestParam("tel") String tel, Model model) {
        if (userRepository.findByLoginid(loginid) != null) {
            model.addAttribute("error", "아이디가 이미 존재합니다.");
            return "user/save";
        }
        if (userRepository.findByEmail(email) != null) {
            model.addAttribute("error", "이메일이 이미 존재합니다.");
            return "user/save";
        }
        if (userRepository.findByNickname(nickname) != null) {
            model.addAttribute("error", "별명이 이미 존재합니다.");
            return "user/save";
        }

        UserEntity user = new UserEntity();
        user.setLoginid(loginid);
        user.setEmail(email);
        user.setPw(pw); // 암호화 함?
        user.setSignupType(signupType); // 변경된 부분
        user.setNickname(nickname);
        user.setName(name);
        user.setAddress(address);
        user.setTel(tel);
        userRepository.save(user);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("loginid") String loginid, @RequestParam("pw") String pw, Model model) {
        UserEntity user = userRepository.findByLoginid(loginid);
        if (user != null && user.getPw().equals(pw)) {
            try {
                String encodedNickname = URLEncoder.encode(user.getNickname(), StandardCharsets.UTF_8.toString());
                return "redirect:/userWelcome?nickname=" + encodedNickname;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        model.addAttribute("error", "비밀번호가 틀립니다.");
        return "login";
    }

    @GetMapping("/check-duplicate")
    @ResponseBody
    public Map<String, Boolean> checkDuplicate(@RequestParam("field") String field, @RequestParam("value") String value) {
        boolean exists = false;
        switch (field) {
            case "loginid":
                exists = userRepository.findByLoginid(value) != null;
                break;
            case "email":
                exists = userRepository.findByEmail(value) != null;
                break;
            case "nickname":
                exists = userRepository.findByNickname(value) != null;
                break;
        }
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return response;
    }

    @GetMapping("/userWelcome")
    public String welcomePage(@RequestParam("nickname") String nickname, Model model) {
        model.addAttribute("nickname", nickname);
        List<UserEntity> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "welcome";
    }
}
