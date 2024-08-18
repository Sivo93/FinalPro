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

import com.example.myweb.board.dto.FreeBoardCommentDTO;
import com.example.myweb.board.dto.FreeBoardDTO;
import com.example.myweb.board.entity.FreeBoardEntity;
import com.example.myweb.board.service.FreeBoardCommentService;
import com.example.myweb.board.service.FreeBoardService;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/freeboard")
public class FreeBoardController {
	private final FreeBoardService freeBoardService;
	private final FreeBoardCommentService freeBoardCommentService;

	@GetMapping("/save")
	public String saveForm(Model model) {
		System.out.println("FreeboardController.saveForm 호출");
		// 인기글 가져오기 - 상위 3개만 가져오기
		List<FreeBoardDTO> popularPosts = freeBoardService.getTop3PopularPosts();

		// 모델에 인기글 추가
		model.addAttribute("popularPosts", popularPosts);
		return "freeboard/save.html";
	}

	@PostMapping("/save")
	public String save(@ModelAttribute FreeBoardDTO freeBoardDTO) throws IllegalStateException, IOException {
		System.out.println("freeBoardDTO: " + freeBoardDTO);
		freeBoardService.save(freeBoardDTO);
		return "redirect:/freeboard/paging";
	}



	@GetMapping("/boardList")
	public String findAll(Model model) {
		// DB에서 전체 게시글 데이터를 가져와서 list.html에 보여준다.
		List<FreeBoardDTO> freeBoardDTOList = freeBoardService.findAll();
		model.addAttribute("freeBoardList", freeBoardDTOList);

		return "freeboard/boardList.html";
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
	        freeBoardService.incrementViews(seq);
	        // 이 게시글을 세션에 조회한 목록에 추가합니다.
	        viewedBoardIds.add(seq);
	        session.setAttribute("viewedBoardIds", viewedBoardIds);
	    }

	    // 게시글 데이터를 가져와서 detail.html에 출력
	    FreeBoardDTO freeBoardDTO = freeBoardService.findBySeq(seq);
	    List<FreeBoardCommentDTO> freeBoardCommentDTOList = freeBoardCommentService.findAll(seq);
	    
	    // 세션에서 로그인 사용자 ID를 가져옵니다.
	    String loginid = (String) session.getAttribute("loginid");
	    String nickname = (String) session.getAttribute("nickname");
	    
	    // 로그인 사용자가 작성자인지 여부를 확인합니다.
	    boolean isAuthor = loginid != null && loginid.equals(freeBoardDTO.getLoginid());
	    
	    // 모델에 필요한 데이터 추가
	    model.addAttribute("freeBoardCommentList", freeBoardCommentDTOList);
	    model.addAttribute("freeBoard", freeBoardDTO);
	    model.addAttribute("page", page); // 현재 페이지 정보를 추가
	    model.addAttribute("isAuthor", isAuthor); // 작성자 여부를 모델에 추가
	    model.addAttribute("nickname", nickname); // 로그인 사용자 ID 추가
	    
	    // 인기글 가져오기 - 상위 3개만 가져오기
	    List<FreeBoardDTO> popularPosts = freeBoardService.getTop3PopularPosts();
	    model.addAttribute("popularPosts", popularPosts);
	    
	    return "freeboard/detail.html";
	}

	@GetMapping("/update/{seq}")
	public String updateForm(@PathVariable Long seq, Model model) {
		FreeBoardDTO freeBoardDTO = freeBoardService.findBySeq(seq);
		model.addAttribute("freeBoardUpdate", freeBoardDTO);
		// 인기글 가져오기 - 상위 3개만 가져오기
		List<FreeBoardDTO> popularPosts = freeBoardService.getTop3PopularPosts();

		// 모델에 인기글 추가
		model.addAttribute("popularPosts", popularPosts);
		return "freeboard/update.html";
	}

	@PostMapping("/update")
	public String update(@ModelAttribute FreeBoardDTO freeBoardDTO, Model model) {
	    FreeBoardDTO updatedBoard = freeBoardService.update(freeBoardDTO);
	    
	    // 인기글 가져오기 - 상위 3개만 가져오기
	    List<FreeBoardDTO> popularPosts = freeBoardService.getTop3PopularPosts();
	    model.addAttribute("popularPosts", popularPosts);

	    // 게시글 수정 후 상세 페이지로 이동
	    return "redirect:/freeboard/" + updatedBoard.getSeq();
	}


	@GetMapping("/delete/{seq}")
	public String delete(@PathVariable Long seq) {
		freeBoardService.delete(seq);

		return "redirect:/freeboard/paging";
	}
	
	@GetMapping("/paging")
	public String paging(@PageableDefault(page = 1) Pageable pageable,
	                     @RequestParam(required = false) String tag,
	                     @RequestParam(required = false) String search,
	                     Model model) {
		
	    // 서비스에서 페이지와 필터 조건을 전달하여 데이터를 가져옵니다.
	    Page<FreeBoardDTO> freeBoardList = freeBoardService.paging(pageable, tag, search);
	    List<FreeBoardDTO> popularPosts = freeBoardService.getTop3PopularPosts();
	    int blockLimit = 10;
	    int startPage = (((int) (Math.ceil((double) pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1;
	    int endPage = ((startPage + blockLimit - 1) < freeBoardList.getTotalPages()) 
	                  ? startPage + blockLimit - 1 
	                  : freeBoardList.getTotalPages();

	    model.addAttribute("freeBoardList", freeBoardList);
	    model.addAttribute("startPage", startPage);
	    model.addAttribute("endPage", endPage);
	    model.addAttribute("popularPosts", popularPosts);
	    model.addAttribute("currentTag", tag);
	    model.addAttribute("searchKeyword", search);

	    return "freeboard/paging.html";
	}

	// 좋아요 기능
	@PostMapping("/like")
	public @ResponseBody String toggleLike(@RequestParam Long boardSeq, HttpSession session) {
		String loginid = (String) session.getAttribute("loginid");
		if (loginid == null) {
			return "not_logged_in";
		}
		boolean success = freeBoardService.toggleLike(boardSeq, loginid);
		return success ? "success" : "fail";
	}

	@PostMapping("/isliked")
	public ResponseEntity<Boolean> isLiked(@RequestParam Long boardSeq, HttpSession session) {
		String loginid = (String) session.getAttribute("loginid");
		if (loginid == null) {
			return ResponseEntity.ok(false); // 세션이 없으면 false 반환
		}
		boolean liked = freeBoardService.isLikedByUser(boardSeq, loginid);
		return ResponseEntity.ok(liked); // 좋아요 여부를 JSON 형태로 반환
	}




}