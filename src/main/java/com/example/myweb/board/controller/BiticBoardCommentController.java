package com.example.myweb.board.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.myweb.board.dto.BiticBoardCommentDTO;
import com.example.myweb.board.service.BiticBoardCommentService;
import com.example.myweb.user.dto.UserDTO;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment/bitic")
public class BiticBoardCommentController {
	private final BiticBoardCommentService biticBoardCommentService;

	@PostMapping("/save")
	public ResponseEntity save(@ModelAttribute BiticBoardCommentDTO biticBoardCommentDTO) {
		System.out.println("biticBoardCommentDTO = " + biticBoardCommentDTO);
		Long saveResult = biticBoardCommentService.save(biticBoardCommentDTO);
		if (saveResult != null) {
			// 작성성공 하면 댓글목록을 가져와서 리턴
			// 댓글목록: 해당 게시글의 댓글 전체
			List<BiticBoardCommentDTO> biticBoardCommentDTOList = biticBoardCommentService
					.findAll(biticBoardCommentDTO.getBiticBoardSeq());

			return new ResponseEntity<>(biticBoardCommentDTOList, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("해당 게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
	}

    // 댓글 수정 요청 처리
    @PostMapping("/update")
    public @ResponseBody String update(@RequestParam("commentSeq") Long commentSeq,
                                        @RequestParam("newContent") String newContent) {
        // 로그 출력
        System.out.println("commentSeq: " + commentSeq);
        System.out.println("newContent: " + newContent);

        // 서비스 메서드를 호출하여 댓글 수정 처리
        Long updatedSeq = biticBoardCommentService.updateComment(commentSeq, newContent);

        if (updatedSeq != null) {
            return "수정 성공";  // 성공 응답
        } else {
            return "수정 실패";  // 실패 응답
        }
    }

	@PostMapping("/delete")
	public @ResponseBody String deleteComment(@RequestParam("commentSeq") Long commentSeq) {
		System.out.println("딜리트매핑 진입 완료" + commentSeq);
		boolean success = biticBoardCommentService.deleteComment(commentSeq);

		if (success) {
			return "삭제 성공"; // 성공 시 200 OK 응답
		} else {
			return "삭제 실패"; // 실패 시 404 Not Found 응답
		}
	}

}