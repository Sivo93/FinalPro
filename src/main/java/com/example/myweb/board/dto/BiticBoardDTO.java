package com.example.myweb.board.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.myweb.board.entity.BiticBoardCommentEntity;
import com.example.myweb.board.entity.BiticBoardEntity;
import com.example.myweb.board.entity.BiticBoardFileEntity;

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
public class BiticBoardDTO {
    private Long seq; // 글쓰기번호
    private String tag; // 태그
    private String title; // 제목
    private LocalDateTime createdTime; // 작성시간
    private int views; // 조회수
    private int likeCount; // 추천 수
    private String contents; // 내용

    private String nickname; // 작성자
    private String loginid; // 회원아이디

    private List<MultipartFile> biticboardFile; // save.html -> Controller 파일 담는 용도
    private List<String> originalFileName; // 원본 파일 이름
    private List<String> storedFileName; // 서버 저장용 파일 이름
    private int fileAttached; // 파일 첨부 여부(첨부 1, 미첨부 0)

    private List<BiticBoardCommentDTO> biticBoardCommentList; // 댓글 리스트 추가

    // 목록: seq, nickname, title, views, likeCount, createdTime

    public static BiticBoardDTO toBiticBoardDTO(BiticBoardEntity biticBoardEntity) {
        BiticBoardDTO biticBoardDTO = new BiticBoardDTO();
        biticBoardDTO.setSeq(biticBoardEntity.getSeq());
        biticBoardDTO.setTag(biticBoardEntity.getTag());
        biticBoardDTO.setTitle(biticBoardEntity.getTitle());
        biticBoardDTO.setCreatedTime(biticBoardEntity.getCreatedTime());
        biticBoardDTO.setViews(biticBoardEntity.getViews());
        biticBoardDTO.setLikeCount(biticBoardEntity.getLikeCount());
        biticBoardDTO.setContents(biticBoardEntity.getContents());
        biticBoardDTO.setNickname(biticBoardEntity.getNickname());
        biticBoardDTO.setLoginid(biticBoardEntity.getLoginid());

        if (biticBoardEntity.getFileAttached() == 0) {
        	biticBoardDTO.setFileAttached(biticBoardEntity.getFileAttached()); // 0
        } else {
            List<String> originalFileNameList = new ArrayList<>();
            List<String> storedFileNameList = new ArrayList<>();
            biticBoardDTO.setFileAttached(biticBoardEntity.getFileAttached()); // 1
            for(BiticBoardFileEntity biticBoardFileEntity : biticBoardEntity.getBiticBoardFileEntityList()) {
                originalFileNameList.add(biticBoardFileEntity.getOriginalFileName());
                storedFileNameList.add(biticBoardFileEntity.getStoredFileName());
            }
            biticBoardDTO.setOriginalFileName(originalFileNameList);
            biticBoardDTO.setStoredFileName(storedFileNameList);
        }

        // 댓글 리스트 설정
        List<BiticBoardCommentDTO> commentDTOList = new ArrayList<>();
        for (BiticBoardCommentEntity commentEntity : biticBoardEntity.getBiticBoardCommentEntityList()) {
            BiticBoardCommentDTO commentDTO = BiticBoardCommentDTO.toBiticBoardCommentDTO(commentEntity);
            commentDTOList.add(commentDTO);
        }
        biticBoardDTO.setBiticBoardCommentList(commentDTOList);

        return biticBoardDTO;
    }

    public BiticBoardDTO(Long seq, String tag, String title, LocalDateTime createdTime, int views, int likeCount,
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
