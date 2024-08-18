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

import com.example.myweb.board.dto.BiticBoardCommentDTO;
import com.example.myweb.board.service.BiticBoardCommentService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class BiticBoardCommentController {
	private final BiticBoardCommentService biticBoardCommentService;
	
	@PostMapping("/bitic/save")
	@ResponseBody
	public ResponseEntity<?> save(@RequestBody BiticBoardCommentDTO biticBoardCommentDTO) {
	    System.out.println("biticBoardCommentDTO = " + biticBoardCommentDTO);
	    Long saveResult = biticBoardCommentService.save(biticBoardCommentDTO);
	    if(saveResult != null) {
	        List<BiticBoardCommentDTO> biticBoardCommentDTOList = biticBoardCommentService.findAll(biticBoardCommentDTO.getBiticBoardSeq());
	        return new ResponseEntity<>(biticBoardCommentDTOList, HttpStatus.OK);
	    } else {
	        return new ResponseEntity<>("해당 게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
	    }
	}


}