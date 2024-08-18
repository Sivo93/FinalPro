package com.example.myweb.board.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.example.myweb.board.entity.NoticeBoardCommentEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NoticeBoardCommentDTO {
	private Long seq;
	private String nickname;
	private String loginid;
	private String commentContents;
	private Long noticeBoardSeq;
	private LocalDateTime commentCreatedTime;
	

	// 포맷팅된 시간 반환 메서드 추가
    public String getFormattedCommentCreatedTime() {
        if (commentCreatedTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return commentCreatedTime.format(formatter);
        }
        return null;
    }

    public static NoticeBoardCommentDTO toNoticeBoardCommentDTO(NoticeBoardCommentEntity noticeBoardCommentEntity) {
        NoticeBoardCommentDTO noticeBoardCommentDTO = new NoticeBoardCommentDTO();
        noticeBoardCommentDTO.setSeq(noticeBoardCommentEntity.getSeq());
        noticeBoardCommentDTO.setNickname(noticeBoardCommentEntity.getUser().getNickname());
        noticeBoardCommentDTO.setLoginid(noticeBoardCommentEntity.getUser().getLoginid());
        noticeBoardCommentDTO.setCommentContents(noticeBoardCommentEntity.getCommentContentes());
        noticeBoardCommentDTO.setCommentCreatedTime(noticeBoardCommentEntity.getCreatedTime());
        noticeBoardCommentDTO.setNoticeBoardSeq(noticeBoardCommentEntity.getNoticeBoardEntity().getSeq());

        return noticeBoardCommentDTO;
    }
	
	

	

	
	
}
