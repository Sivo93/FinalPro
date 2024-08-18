package com.example.myweb.board.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.example.myweb.board.entity.BiticBoardCommentEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BiticBoardCommentDTO {
	private Long seq;
	private String nickname;
	private String loginid;
	private String commentContents;
	private Long biticBoardSeq;
	private LocalDateTime commentCreatedTime;
	

	// 포맷팅된 시간 반환 메서드 추가
    public String getFormattedCommentCreatedTime() {
        if (commentCreatedTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return commentCreatedTime.format(formatter);
        }
        return null;
    }

    public static BiticBoardCommentDTO toBiticBoardCommentDTO(BiticBoardCommentEntity biticBoardCommentEntity) {
        BiticBoardCommentDTO biticBoardCommentDTO = new BiticBoardCommentDTO();
        biticBoardCommentDTO.setSeq(biticBoardCommentEntity.getSeq());
        biticBoardCommentDTO.setNickname(biticBoardCommentEntity.getUser().getNickname());
        biticBoardCommentDTO.setLoginid(biticBoardCommentEntity.getUser().getLoginid());
        biticBoardCommentDTO.setCommentContents(biticBoardCommentEntity.getCommentContentes());
        biticBoardCommentDTO.setCommentCreatedTime(biticBoardCommentEntity.getCreatedTime());
        biticBoardCommentDTO.setBiticBoardSeq(biticBoardCommentEntity.getBiticBoardEntity().getSeq());

        return biticBoardCommentDTO;
    }
	
	

	

	
	
}
