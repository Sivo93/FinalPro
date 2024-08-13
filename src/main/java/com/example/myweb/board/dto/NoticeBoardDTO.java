package com.example.myweb.board.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.myweb.board.entity.NoticeBoardCommentEntity;
import com.example.myweb.board.entity.NoticeBoardEntity;
import com.example.myweb.board.entity.NoticeBoardFileEntity;

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
public class NoticeBoardDTO {
    private Long seq; // 글쓰기번호
    private String tag; // 태그
    private String title; // 제목
    private LocalDateTime createdTime; // 작성시간
    private int views; // 조회수
    private int likeCount; // 추천 수
    private String contents; // 내용

    private String nickname; // 작성자
    private String loginid; // 회원아이디

    private List<MultipartFile> noticeboardFile; // save.html -> Controller 파일 담는 용도
    private List<String> originalFileName; // 원본 파일 이름
    private List<String> storedFileName; // 서버 저장용 파일 이름
    private int fileAttached; // 파일 첨부 여부(첨부 1, 미첨부 0)

    private List<NoticeBoardCommentDTO> noticeBoardCommentList; // 댓글 리스트 추가

    // 목록: seq, nickname, title, views, likeCount, createdTime

    public static NoticeBoardDTO toNoticeBoardDTO(NoticeBoardEntity noticeBoardEntity) {
        NoticeBoardDTO noticeBoardDTO = new NoticeBoardDTO();
        noticeBoardDTO.setSeq(noticeBoardEntity.getSeq());
        noticeBoardDTO.setTag(noticeBoardEntity.getTag());
        noticeBoardDTO.setTitle(noticeBoardEntity.getTitle());
        noticeBoardDTO.setCreatedTime(noticeBoardEntity.getCreatedTime());
        noticeBoardDTO.setViews(noticeBoardEntity.getViews());
        noticeBoardDTO.setLikeCount(noticeBoardEntity.getLikeCount());
        noticeBoardDTO.setContents(noticeBoardEntity.getContents());
        noticeBoardDTO.setNickname(noticeBoardEntity.getNickname());
        noticeBoardDTO.setLoginid(noticeBoardEntity.getLoginid());

        if (noticeBoardEntity.getFileAttached() == 0) {
        	noticeBoardDTO.setFileAttached(noticeBoardEntity.getFileAttached()); // 0
        } else {
            List<String> originalFileNameList = new ArrayList<>();
            List<String> storedFileNameList = new ArrayList<>();
            noticeBoardDTO.setFileAttached(noticeBoardEntity.getFileAttached()); // 1
            for(NoticeBoardFileEntity noticeBoardFileEntity : noticeBoardEntity.getNoticeBoardFileEntityList()) {
                originalFileNameList.add(noticeBoardFileEntity.getOriginalFileName());
                storedFileNameList.add(noticeBoardFileEntity.getStoredFileName());
            }
            noticeBoardDTO.setOriginalFileName(originalFileNameList);
            noticeBoardDTO.setStoredFileName(storedFileNameList);
        }

        // 댓글 리스트 설정
        List<NoticeBoardCommentDTO> commentDTOList = new ArrayList<>();
        for (NoticeBoardCommentEntity commentEntity : noticeBoardEntity.getNoticeBoardCommentEntityList()) {
            NoticeBoardCommentDTO commentDTO = NoticeBoardCommentDTO.toNoticeBoardCommentDTO(commentEntity);
            commentDTOList.add(commentDTO);
        }
        noticeBoardDTO.setNoticeBoardCommentList(commentDTOList);

        return noticeBoardDTO;
    }

    public NoticeBoardDTO(Long seq, String tag, String title, LocalDateTime createdTime, int views, int likeCount,
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
