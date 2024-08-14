package com.example.myweb.board.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.myweb.board.entity.FreeBoardEntity;
import com.example.myweb.board.entity.FreeBoardFileEntity;
import com.example.myweb.board.entity.FreeBoardCommentEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FreeBoardDTO {
    private Long seq; // 글쓰기번호
    private String tag; // 태그
    private String title; // 제목
    private LocalDateTime createdTime; // 작성시간
    private int views; // 조회수
    private int likeCount; // 추천 수
    private String contents; // 내용

    private String nickname; // 작성자
    private String loginid; // 회원아이디

    private List<MultipartFile> freeboardFile; // save.html -> Controller 파일 담는 용도
    private List<String> originalFileName; // 원본 파일 이름
    private List<String> storedFileName; // 서버 저장용 파일 이름
    private int fileAttached; // 파일 첨부 여부(첨부 1, 미첨부 0)

    private List<FreeBoardCommentDTO> freeBoardCommentList; // 댓글 리스트 추가

    // 목록: seq, nickname, title, views, likeCount, createdTime

    public static FreeBoardDTO toFreeBoardDTO(FreeBoardEntity freeBoardEntity) {
        FreeBoardDTO freeBoardDTO = new FreeBoardDTO();
        freeBoardDTO.setSeq(freeBoardEntity.getSeq());
        freeBoardDTO.setTag(freeBoardEntity.getTag());
        freeBoardDTO.setTitle(freeBoardEntity.getTitle());
        freeBoardDTO.setCreatedTime(freeBoardEntity.getCreatedTime());
        freeBoardDTO.setViews(freeBoardEntity.getViews());
        freeBoardDTO.setLikeCount(freeBoardEntity.getLikeCount());
        freeBoardDTO.setContents(freeBoardEntity.getContents());
        freeBoardDTO.setNickname(freeBoardEntity.getNickname());
        freeBoardDTO.setLoginid(freeBoardEntity.getLoginid());

        if (freeBoardEntity.getFileAttached() == 0) {
            freeBoardDTO.setFileAttached(freeBoardEntity.getFileAttached()); // 0
        } else {
            List<String> originalFileNameList = new ArrayList<>();
            List<String> storedFileNameList = new ArrayList<>();
            freeBoardDTO.setFileAttached(freeBoardEntity.getFileAttached()); // 1
            for(FreeBoardFileEntity freeBoardFileEntity : freeBoardEntity.getFreeBoardFileEntityList()) {
                originalFileNameList.add(freeBoardFileEntity.getOriginalFileName());
                storedFileNameList.add(freeBoardFileEntity.getStoredFileName());
            }
            freeBoardDTO.setOriginalFileName(originalFileNameList);
            freeBoardDTO.setStoredFileName(storedFileNameList);
        }

        // 댓글 리스트 설정
        List<FreeBoardCommentDTO> commentDTOList = new ArrayList<>();
        for (FreeBoardCommentEntity commentEntity : freeBoardEntity.getFreeBoardCommentEntityList()) {
            FreeBoardCommentDTO commentDTO = FreeBoardCommentDTO.toFreeBoardCommentDTO(commentEntity);
            commentDTOList.add(commentDTO);
        }
        freeBoardDTO.setFreeBoardCommentList(commentDTOList);

        return freeBoardDTO;
    }

    public FreeBoardDTO(Long seq, String tag, String title, LocalDateTime createdTime, int views, int likeCount,
                        String nickname) {
        this.seq = seq;
        this.tag = tag;
        this.title = title;
        this.createdTime = createdTime;
        this.views = views;
        this.likeCount = likeCount;
        this.nickname = nickname;
    }
}
