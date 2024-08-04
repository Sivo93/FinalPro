package com.example.myweb.user.controller;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.myweb.user.dto.UserDTO;
import com.example.myweb.user.security.JWTUtil;
import com.example.myweb.user.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {
	// 생성자 주입
	private final UserService userService;
	private final JWTUtil jwtUtil;

	@GetMapping("/")
	public String index() {
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iter = authorities.iterator();
		GrantedAuthority auth = iter.next();
		String role = auth.getAuthority();
		
		System.out.println("username : " + username);
		System.out.println("role : " + role);

		return "index.html";
	}

	// 회원가입 페이지 출력 요청
	@GetMapping("/user/save")
	public String saveForm() {

		return "user/save.html";
	}

	@PostMapping("/user/save")
	public String save(@ModelAttribute UserDTO userDTO) {
		System.out.println("UserController.save 실행");
		System.out.println("UserDTO : " + userDTO);

		userService.save(userDTO);

		return "user/login.html";
	}

	@GetMapping("/user/login")
	public String loginForm() {

		return "user/login.html";
	}

	@PostMapping("/user/login")
    public String login(@ModelAttribute UserDTO userDTO, HttpSession session, Model model) {
        UserDTO loginResult = userService.login(userDTO);
        if (loginResult != null) {
            // JWT 발급
            String token = jwtUtil.createJwt(loginResult.getLoginid(), loginResult.getRole(), 60 * 60 * 10L);

            // 세션에 로그인 정보 저장
            session.setAttribute("loginid", loginResult.getLoginid());
            session.setAttribute("nickname", loginResult.getNickname());
            session.setAttribute("loginUser", loginResult);

            // JWT 토큰을 모델에 추가 (예: 페이지에서 사용할 수 있도록)
            model.addAttribute("token", token);

            // 로그인 성공 후 이동할 페이지
            return "redirect:/user/main";
        } else {
            // 로그인 실패 시 로그인 페이지로 리다이렉트
            return "redirect:/user/login?error";
        }
    }

//	@PostMapping("/user/login")
//	public String login(@ModelAttribute UserDTO userDTO, HttpSession session) {
//		UserDTO loginResult = userService.login(userDTO);
//		if (loginResult != null) {
//			session.setAttribute("loginid", loginResult.getLoginid());
//			session.setAttribute("nickname", loginResult.getNickname()); // nickname 추가
//			session.setAttribute("loginUser", loginResult);
//			String redirectURL = (String) session.getAttribute("redirectURL");
//			if (redirectURL != null) {
//				session.removeAttribute("redirectURL");
//				return "redirect:" + redirectURL;
//			}
//			return "user/main.html";
//		} else {
//			return "user/login.html";
//		}
//	}

	@GetMapping("/user/userList")
	public String findAll(Model model) {
		List<UserDTO> userDTOList = userService.findAll();
		// 어떠한 html로 가져갈 데이터가 있다면 model 사용
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
		System.out.println("loginid = " + loginid);
		String checkResult = userService.loginidCheck(loginid);

		return checkResult;
	}

	@PostMapping("/user/email-check")
	public @ResponseBody String emailCheck(@RequestParam("email") String email) {
		System.out.println("email = " + email);
		String checkResult = userService.emailCheck(email);

		return checkResult;
	}

	@PostMapping("/user/nickname-check")
	public @ResponseBody String nicknameCheck(@RequestParam("nickname") String nickname) {
		System.out.println("nickname = " + nickname);
		String checkResult = userService.nicknameCheck(nickname);

		return checkResult;
	}

	@GetMapping("/api/check-login")
	public ResponseEntity<Boolean> checkLogin(HttpSession session) {
		String loginid = (String) session.getAttribute("loginid");
		boolean isLoggedIn = loginid != null;
		return ResponseEntity.ok(isLoggedIn);
	}
}
