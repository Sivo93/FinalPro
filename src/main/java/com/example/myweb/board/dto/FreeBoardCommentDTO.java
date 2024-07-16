package com.example.myweb.board.dto;

import java.time.LocalDateTime;

import com.example.myweb.board.entity.FreeBoardCommentEntity;
import com.example.myweb.user.dto.UserDTO;

import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FreeBoardCommentDTO {
	private Long seq;
	private String nickname;
	private String loginid;
	private String commentContents;
	private Long freeBoardSeq;
	private LocalDateTime commentCreatedTime;
	
	
	public static FreeBoardCommentDTO toFreeBoardCommentDTO(FreeBoardCommentEntity freeBoardCommentEntity) {
		FreeBoardCommentDTO freeBoardCommentDTO = new FreeBoardCommentDTO();
		freeBoardCommentDTO.setSeq(freeBoardCommentEntity.getSeq());
		freeBoardCommentDTO.setNickname(freeBoardCommentEntity.getUser().getNickname());
		freeBoardCommentDTO.setLoginid(freeBoardCommentEntity.getUser().getLoginid());
		freeBoardCommentDTO.setCommentContents(freeBoardCommentEntity.getCommentContentes());
		freeBoardCommentDTO.setCommentCreatedTime(freeBoardCommentEntity.getCreatedTime());
		freeBoardCommentDTO.setFreeBoardSeq(freeBoardCommentEntity.getFreeBoardEntity().getSeq());
	
		return freeBoardCommentDTO;
	}
	
	

	

	
	
}
