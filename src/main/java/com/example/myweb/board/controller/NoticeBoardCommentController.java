package com.example.myweb.board.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.myweb.board.dto.NoticeBoardCommentDTO;
import com.example.myweb.board.service.NoticeBoardCommentService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class NoticeBoardCommentController {
	private final NoticeBoardCommentService noticeBoardCommentService;
	
	@PostMapping("/notice/save")
	@ResponseBody
	public ResponseEntity<?> save(@RequestBody NoticeBoardCommentDTO noticeBoardCommentDTO) {
	    System.out.println("noticeBoardCommentDTO = " + noticeBoardCommentDTO);
	    Long saveResult = noticeBoardCommentService.save(noticeBoardCommentDTO);
	    if(saveResult != null) {
	        List<NoticeBoardCommentDTO> noticeBoardCommentDTOList = noticeBoardCommentService.findAll(noticeBoardCommentDTO.getNoticeBoardSeq());
	        return new ResponseEntity<>(noticeBoardCommentDTOList, HttpStatus.OK);
	    } else {
	        return new ResponseEntity<>("해당 게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
	    }
	}


}