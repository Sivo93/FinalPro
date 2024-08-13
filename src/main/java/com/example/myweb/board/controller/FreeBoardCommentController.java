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

import com.example.myweb.board.dto.FreeBoardCommentDTO;
import com.example.myweb.board.service.FreeBoardCommentService;
import com.example.myweb.user.dto.UserDTO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class FreeBoardCommentController {
	private final FreeBoardCommentService freeBoardCommentService;
	
	@PostMapping("/free/save")
	@ResponseBody
	public ResponseEntity<?> save(@RequestBody FreeBoardCommentDTO freeBoardCommentDTO) {
	    System.out.println("freeBoardCommentDTO = " + freeBoardCommentDTO);
	    Long saveResult = freeBoardCommentService.save(freeBoardCommentDTO);
	    if(saveResult != null) {
	        List<FreeBoardCommentDTO> freeBoardCommentDTOList = freeBoardCommentService.findAll(freeBoardCommentDTO.getFreeBoardSeq());
	        return new ResponseEntity<>(freeBoardCommentDTOList, HttpStatus.OK);
	    } else {
	        return new ResponseEntity<>("해당 게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
	    }
	}




}