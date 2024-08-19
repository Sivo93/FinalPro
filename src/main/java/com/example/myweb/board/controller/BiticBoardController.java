package com.example.myweb.board.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.myweb.board.dto.BiticBoardCommentDTO;
import com.example.myweb.board.dto.BiticBoardDTO;
import com.example.myweb.board.entity.BiticBoardEntity;
import com.example.myweb.board.service.BiticBoardCommentService;
import com.example.myweb.board.service.BiticBoardService;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/biticboard")
public class BiticBoardController {
	private final BiticBoardService biticBoardService;
	private final BiticBoardCommentService biticBoardCommentService;

	@GetMapping("/save")
	public String saveForm(Model model) {
		System.out.println("BiticboardController.saveForm 호출");
		// 인기글 가져오기 - 상위 3개만 가져오기
		List<BiticBoardDTO> popularPosts = biticBoardService.getTop3PopularPosts();

		// 모델에 인기글 추가
		model.addAttribute("popularPosts", popularPosts);
		return "biticboard/save.html";
	}

	@PostMapping("/save")
	public String save(@ModelAttribute BiticBoardDTO biticBoardDTO) throws IllegalStateException, IOException {
		System.out.println("biticBoardDTO: " + biticBoardDTO);
		biticBoardService.save(biticBoardDTO);
		return "redirect:/biticboard/paging";
	}



	@GetMapping("/boardList")
	public String findAll(Model model) {
		// DB에서 전체 게시글 데이터를 가져와서 list.html에 보여준다.
		List<BiticBoardDTO> biticBoardDTOList = biticBoardService.findAll();
		model.addAttribute("biticBoardList", biticBoardDTOList);

		return "biticboard/boardList.html";
	}
	

	@GetMapping("/{seq}")
	public String findBySeq(@PathVariable Long seq,
	                        @RequestParam(defaultValue = "1") int page, // 현재 페이지를 파라미터로 받음
	                        Model model, 
	                        HttpSession session,
	                        @PageableDefault(page = 1) Pageable pageable) {
	    
	    // 세션에서 조회한 게시글 ID 목록을 가져옵니다.
	    Set<Long> viewedBoardIds = (Set<Long>) session.getAttribute("viewedBoardIds");
	    if (viewedBoardIds == null) {
	        viewedBoardIds = new HashSet<>();
	        session.setAttribute("viewedBoardIds", viewedBoardIds);
	    }

	    // 이 게시글이 이전에 조회된 적 있는지 확인합니다.
	    if (!viewedBoardIds.contains(seq)) {
	        // 조회수 증가 로직을 수행합니다.
	        biticBoardService.incrementViews(seq);
	        // 이 게시글을 세션에 조회한 목록에 추가합니다.
	        viewedBoardIds.add(seq);
	        session.setAttribute("viewedBoardIds", viewedBoardIds);
	    }

	    // 게시글 데이터를 가져와서 detail.html에 출력
	    BiticBoardDTO biticBoardDTO = biticBoardService.findBySeq(seq);
	    List<BiticBoardCommentDTO> biticBoardCommentDTOList = biticBoardCommentService.findAll(seq);
	    
	    // 세션에서 로그인 사용자 ID를 가져옵니다.
	    String loginid = (String) session.getAttribute("loginid");
	    String nickname = (String) session.getAttribute("nickname");
	    String role = (String) session.getAttribute("role"); // 역할 정보 가져오기
	    // 로그인 사용자가 작성자인지 여부를 확인합니다.
	    boolean isAuthor = loginid != null && loginid.equals(biticBoardDTO.getLoginid());
	    // 사용자가 관리자(admin)인지 확인
	    boolean isAdmin = role != null && role.equals("admin");
	    // 모델에 필요한 데이터 추가
	    model.addAttribute("biticBoardCommentList", biticBoardCommentDTOList);
	    model.addAttribute("biticBoard", biticBoardDTO);
	    model.addAttribute("page", page); // 현재 페이지 정보를 추가
	    model.addAttribute("isAuthor", isAuthor); // 작성자 여부를 모델에 추가
	    model.addAttribute("nickname", nickname); // 로그인 사용자 ID 추가
	    model.addAttribute("isAdmin", isAdmin);  // 관리자 여부 추가
	    // 인기글 가져오기 - 상위 3개만 가져오기
	    List<BiticBoardDTO> popularPosts = biticBoardService.getTop3PopularPosts();
	    model.addAttribute("popularPosts", popularPosts);
	    
	    return "biticboard/detail.html";
	}

	@GetMapping("/update/{seq}")
	public String updateForm(@PathVariable Long seq, Model model) {
		BiticBoardDTO biticBoardDTO = biticBoardService.findBySeq(seq);
		model.addAttribute("biticBoardUpdate", biticBoardDTO);
		// 인기글 가져오기 - 상위 3개만 가져오기
		List<BiticBoardDTO> popularPosts = biticBoardService.getTop3PopularPosts();

		// 모델에 인기글 추가
		model.addAttribute("popularPosts", popularPosts);
		return "biticboard/update.html";
	}

	@PostMapping("/update")
	public String update(@ModelAttribute BiticBoardDTO biticBoardDTO, Model model) {
	    BiticBoardDTO updatedBoard = biticBoardService.update(biticBoardDTO);
	    
	    // 인기글 가져오기 - 상위 3개만 가져오기
	    List<BiticBoardDTO> popularPosts = biticBoardService.getTop3PopularPosts();
	    model.addAttribute("popularPosts", popularPosts);

	    // 게시글 수정 후 상세 페이지로 이동
	    return "redirect:/biticboard/" + updatedBoard.getSeq();
	}


	@GetMapping("/delete/{seq}")
	public String delete(@PathVariable Long seq) {
		biticBoardService.delete(seq);

		return "redirect:/biticboard/paging";
	}
	
	@GetMapping("/paging")
	public String paging(@PageableDefault(page = 1) Pageable pageable,
	                     @RequestParam(required = false) String tag,
	                     @RequestParam(required = false) String search,
	                     Model model) {
		
	    // 서비스에서 페이지와 필터 조건을 전달하여 데이터를 가져옵니다.
	    Page<BiticBoardDTO> biticBoardList = biticBoardService.paging(pageable, tag, search);
	    List<BiticBoardDTO> popularPosts = biticBoardService.getTop3PopularPosts();
	    int blockLimit = 10;
	    int startPage = (((int) (Math.ceil((double) pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1;
	    int endPage = ((startPage + blockLimit - 1) < biticBoardList.getTotalPages()) 
	                  ? startPage + blockLimit - 1 
	                  : biticBoardList.getTotalPages();

	    model.addAttribute("biticBoardList", biticBoardList);
	    model.addAttribute("startPage", startPage);
	    model.addAttribute("endPage", endPage);
	    model.addAttribute("popularPosts", popularPosts);
	    model.addAttribute("currentTag", tag);
	    model.addAttribute("searchKeyword", search);

	    return "biticboard/paging.html";
	}

	// 좋아요 기능
	@PostMapping("/like")
	public @ResponseBody String toggleLike(@RequestParam Long boardSeq, HttpSession session) {
		String loginid = (String) session.getAttribute("loginid");
		if (loginid == null) {
			return "not_logged_in";
		}
		boolean success = biticBoardService.toggleLike(boardSeq, loginid);
		return success ? "success" : "fail";
	}

	@PostMapping("/isliked")
	public ResponseEntity<Boolean> isLiked(@RequestParam Long boardSeq, HttpSession session) {
		String loginid = (String) session.getAttribute("loginid");
		if (loginid == null) {
			return ResponseEntity.ok(false); // 세션이 없으면 false 반환
		}
		boolean liked = biticBoardService.isLikedByUser(boardSeq, loginid);
		return ResponseEntity.ok(liked); // 좋아요 여부를 JSON 형태로 반환
	}




}