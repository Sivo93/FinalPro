package com.example.myweb.board.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
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

import com.example.myweb.board.dto.FreeBoardCommentDTO;
import com.example.myweb.board.dto.FreeBoardDTO;
import com.example.myweb.board.service.FreeBoardCommentService;
import com.example.myweb.board.service.FreeBoardService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/freeboard")
public class FreeBoardController {
	private final FreeBoardService freeBoardService;
	private final FreeBoardCommentService freeBoardCommentService;

	@GetMapping("/save")
	public String saveForm() {
		System.out.println("FreeboardController.saveForm 호출");
		return "freeboard/save.html";
	}

	@PostMapping("/save")
	public String save(@ModelAttribute FreeBoardDTO freeBoardDTO) throws IllegalStateException, IOException {
		System.out.println("freeBoardDTO: " + freeBoardDTO);
		freeBoardService.save(freeBoardDTO);
		return "user/main.html";
	}

	@GetMapping("/boardList")
	public String findAll(Model model) {
		// DB에서 전체 게시글 데이터를 가져와서 list.html에 보여준다.
		List<FreeBoardDTO> freeBoardDTOList = freeBoardService.findAll();
		model.addAttribute("freeBoardList", freeBoardDTOList);

		return "freeboard/boardList.html";
	}

//	@GetMapping("/{seq}")
//	public String findBySeq(@PathVariable Long seq, Model model, @PageableDefault(page = 1) Pageable pageable) {
//		/*
//		 * 해당 게시글의 조회수를 하나 올리고 게시글 데이터를 가져와서 detail.html에 출력
//		 */
//		freeBoardService.incrementViews(seq);
//		FreeBoardDTO freeBoardDTO = freeBoardService.findBySeq(seq);
////		댓글 목록 가져오기
//		List<FreeBoardCommentDTO> freeBoardCommentDTOList = freeBoardCommentService.findAll(seq);
//
//		model.addAttribute("freeBoardCommentList", freeBoardCommentDTOList);
//		model.addAttribute("freeBoard", freeBoardDTO);
//		model.addAttribute("page", pageable.getPageNumber());
//		System.out.println(freeBoardDTO.getStoredFileName());
//		System.out.println(freeBoardDTO.getFileAttached());
//
//		return "freeboard/detail.html";
//	}

	@GetMapping("/{seq}")
	public String findBySeq(@PathVariable Long seq, Model model, HttpSession session,
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

		model.addAttribute("freeBoardCommentList", freeBoardCommentDTOList);
		model.addAttribute("freeBoard", freeBoardDTO);
		model.addAttribute("page", pageable.getPageNumber());

		return "freeboard/detail.html";
	}

	@GetMapping("/update/{seq}")
	public String updateForm(@PathVariable Long seq, Model model) {
		FreeBoardDTO freeBoardDTO = freeBoardService.findBySeq(seq);
		model.addAttribute("freeBoardUpdate", freeBoardDTO);

		return "freeboard/update.html";
	}

	@PostMapping("/update")
	public String update(@ModelAttribute FreeBoardDTO freeBoardDTO, Model model) {
		FreeBoardDTO freeBoard = freeBoardService.update(freeBoardDTO);
		model.addAttribute("freeBoard", freeBoard);

		return "freeboard/detail.html";
	}

	@GetMapping("/delete/{seq}")
	public String delete(@PathVariable Long seq) {
		freeBoardService.delete(seq);

		return "redirect:/freeboard/boardList";
	}

	// /freeboard/paging?page=1
	@GetMapping("/paging")
	public String paging(@PageableDefault(page = 1) Pageable pageable, Model model) {
//		pageable.getPageNumber();
		Page<FreeBoardDTO> freeBoardList = freeBoardService.paging(pageable);
		int blockLimit = 3;
		int startPage = (((int) (Math.ceil((double) pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1; // 1
																													// 4
																													// 7
																													// 10
																													// ~~
		int endPage = ((startPage + blockLimit - 1) < freeBoardList.getTotalPages()) ? startPage + blockLimit - 1
				: freeBoardList.getTotalPages();
		// page 갯수 20개
		// 현재 사용자가 3페이지
		// 1 2 3
		// 현재 사용자가 7페이지
		// 7 8 9
		// 보여지는 페이지 갯수 3개
		// 총 페이지 갯수 8개

		model.addAttribute("freeBoardList", freeBoardList);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);

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
