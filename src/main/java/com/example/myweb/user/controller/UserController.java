package com.example.myweb.user.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.myweb.user.dto.UserDTO;
import com.example.myweb.user.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        String loginid = (String) session.getAttribute("loginid");
        String nickname = (String) session.getAttribute("nickname");
        model.addAttribute("loginid", loginid);
        model.addAttribute("nickname", nickname);

        return "index.html";
    }

    @GetMapping("/user/save")
    public String saveForm() {
        return "user/save.html";
    }

    @PostMapping("/user/save")
    public String save(@ModelAttribute UserDTO userDTO) {
        userService.save(userDTO);
        return "user/login.html";
    }

//    @GetMapping("/user/login")
//    public String loginForm() {
//        return "user/login.html";
//    }
    
    @GetMapping("/user/login")
    public String loginPage(Model model, HttpServletRequest request) {
        // 쿠키에서 저장된 아이디를 읽어옵니다.
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("rememberedId")) {
                    model.addAttribute("rememberedId", cookie.getValue());
                    break;
                }
            }
        }
        return "user/login.html";
    }

//    @PostMapping("/user/login")
//    public String login(@ModelAttribute UserDTO userDTO, HttpSession session) {
//        UserDTO loginResult = userService.login(userDTO);
//        if (loginResult != null) {
//            session.setAttribute("loginid", loginResult.getLoginid());
//            session.setAttribute("nickname", loginResult.getNickname());
//            session.setAttribute("userSeq", loginResult.getSeq());
//            return "redirect:/";
//        } else {
//            return "user/login.html";
//        }
//    }
    
    @PostMapping("/user/login")
    public String login(@ModelAttribute UserDTO userDTO,
                        @RequestParam(value = "rememberMe", defaultValue = "false") boolean rememberId,
                        HttpSession session,
                        HttpServletResponse response) {

        UserDTO loginResult = userService.login(userDTO);
        if (loginResult != null) {
            session.setAttribute("loginid", loginResult.getLoginid());
            session.setAttribute("nickname", loginResult.getNickname());
            session.setAttribute("userSeq", loginResult.getSeq());

            if (rememberId) {
                // 아이디 저장 쿠키 설정
                Cookie cookie = new Cookie("rememberedId", loginResult.getLoginid());
                cookie.setMaxAge(60 * 60 * 24 * 30); // 30일 동안 저장
                cookie.setPath("/");
                response.addCookie(cookie);
            } else {
                // 아이디 저장을 선택하지 않은 경우, 기존 쿠키 삭제
                Cookie cookie = new Cookie("rememberedId", null);
                cookie.setMaxAge(0); // 쿠키 삭제
                cookie.setPath("/");
                response.addCookie(cookie);
            }

            return "redirect:/";
        } else {
            return "user/login.html";
        }
    }

    @GetMapping("/user/userList")
    public String findAll(Model model) {
        List<UserDTO> userDTOList = userService.findAll();
        model.addAttribute("userList", userDTOList);
        return "user/userList.html";
    }

    @GetMapping("/user/{seq}")
    public String findBySeq(@PathVariable Long seq, Model model) {
        UserDTO userDTO = userService.findBySeq(seq);
        model.addAttribute("user", userDTO);
        return "user/detail.html";
    }

    @GetMapping("/user/update")
    public String updateForm(HttpSession session, Model model) {
        String myLoginid = (String) session.getAttribute("loginid");
        UserDTO userDTO = userService.updateForm(myLoginid);
        model.addAttribute("updateUser", userDTO);
        return "user/update.html";
    }

    @PostMapping("/user/update")
    public String update(@ModelAttribute UserDTO userDTO) {
        userService.update(userDTO);
        return "redirect:/user/" + userDTO.getSeq();
    }

    @GetMapping("/user/delete/{seq}")
    public String deleteBySeq(@PathVariable Long seq) {
        userService.deleteBySeq(seq);
        return "redirect:/user/userList";
    }

    @GetMapping("/user/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @PostMapping("/user/id-check")
    public @ResponseBody String idCheck(@RequestParam("loginid") String loginid) {
        String checkResult = userService.loginidCheck(loginid);
        return checkResult;
    }

    @PostMapping("/user/email-check")
    public @ResponseBody String emailCheck(@RequestParam("email") String email) {
        String checkResult = userService.emailCheck(email);
        return checkResult;
    }

    @PostMapping("/user/nickname-check")
    public @ResponseBody String nicknameCheck(@RequestParam("nickname") String nickname) {
        String checkResult = userService.nicknameCheck(nickname);
        return checkResult;
    }

    @GetMapping("/api/check-login")
    public ResponseEntity<Map<String, Object>> checkLogin(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String loginid = (String) session.getAttribute("loginid");
        String nickname = (String) session.getAttribute("nickname");
        Long userSeq = (Long) session.getAttribute("userSeq");

        if (loginid != null && nickname != null && userSeq != null) {
            response.put("isLoggedIn", true);
            response.put("nickname", nickname);
            response.put("userSeq", userSeq);
        } else {
            response.put("isLoggedIn", false);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/shop")
    public String shop() {
        return "shop";
    }

    @GetMapping("/outside")
    public String outside() {
        return "outside";
    }

    @GetMapping("/user/detail")
    public String userDetail(HttpSession session, Model model) {
        Long userSeq = (Long) session.getAttribute("userSeq");
        if (userSeq == null) {
            return "redirect:/user/login";
        }
        UserDTO userDTO = userService.findBySeq(userSeq);
        model.addAttribute("user", userDTO);
        return "user/detail.html";
    }

}