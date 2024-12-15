package com.example.myweb.board.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.myweb.board.dto.NoticeBoardDTO;

//import java.time.LocalDateTime;

import com.example.myweb.user.entity.UserEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

// DB 테이블 역할을 하는 클래스
@Entity
@Getter
@Setter
@Table(name = "noticeBoard_table")
public class NoticeBoardEntity extends BaseBoardEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    private Long seq;

    @Column(nullable = false)
    private String tag;

    @Column(nullable = false)
    private String title;

//    @Column(nullable = false)
//    private LocalDateTime date;

    @Column
    private int views;

    @Column
    private int likeCount;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String contents;
    
    @Column
    private int fileAttached; // 1 or 0

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "loginid")
    private String loginid;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "nickname", referencedColumnName = "nickname", insertable = false, updatable = false),
        @JoinColumn(name = "loginid", referencedColumnName = "loginid", insertable = false, updatable = false)
    })
    private UserEntity user;
    
    @OneToMany(mappedBy = "noticeBoardEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<NoticeBoardFileEntity> noticeBoardFileEntityList = new ArrayList<>();

    @OneToMany(mappedBy = "noticeBoardEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<NoticeBoardCommentEntity> noticeBoardCommentEntityList = new ArrayList<>();
    
    @OneToMany(mappedBy = "noticeBoardEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<NoticeBoardLikeEntity> likes = new HashSet<>();
  
    // Helper methods to manage the relationship
    public void addFile(NoticeBoardFileEntity file) {
    	noticeBoardFileEntityList.add(file);
        file.setNoticeBoardEntity(this);
    }

    public void removeFile(NoticeBoardFileEntity file) {
        noticeBoardFileEntityList.remove(file);
        file.setNoticeBoardEntity(null);
    }

    public void addComment(NoticeBoardCommentEntity comment) {
        noticeBoardCommentEntityList.add(comment);
        comment.setNoticeBoardEntity(this);
    }

    public void removeComment(NoticeBoardFileEntity comment) {
        noticeBoardCommentEntityList.remove(comment);
        comment.setNoticeBoardEntity(null);
    }

    public void addLike(NoticeBoardLikeEntity like) {
        likes.add(like);
        like.setNoticeBoardEntity(this);
    }
    
    public void removeLike(NoticeBoardLikeEntity like) {
        likes.remove(like);
        like.setNoticeBoardEntity(null);
    }
    
    public static NoticeBoardEntity toSaveEntity(NoticeBoardDTO noticeBoardDTO, UserEntity userEntity) { // 파일이 없는 경우 호출하는 save
    	NoticeBoardEntity noticeBoardEntity = new NoticeBoardEntity();
    	noticeBoardEntity.setTag(noticeBoardDTO.getTag());
    	noticeBoardEntity.setTitle(noticeBoardDTO.getTitle());
    	noticeBoardEntity.setTag(noticeBoardDTO.getTag());
    	noticeBoardEntity.setContents(noticeBoardDTO.getContents());
    	noticeBoardEntity.setViews(0);
    	noticeBoardEntity.setLikeCount(0);
    	noticeBoardEntity.setNickname(noticeBoardDTO.getNickname());
    	noticeBoardEntity.setLoginid(noticeBoardDTO.getLoginid());
    	noticeBoardEntity.setUser(userEntity);
    	noticeBoardEntity.setFileAttached(0); // 파일 없음.
    	
    	return noticeBoardEntity;
    }

    public static NoticeBoardEntity toSaveFileEntity(NoticeBoardDTO noticeBoardDTO, UserEntity userEntity) {
		NoticeBoardEntity noticeBoardEntity = new NoticeBoardEntity();
		noticeBoardEntity.setTag(noticeBoardDTO.getTag());
		noticeBoardEntity.setTitle(noticeBoardDTO.getTitle());
		noticeBoardEntity.setTag(noticeBoardDTO.getTag());
		noticeBoardEntity.setContents(noticeBoardDTO.getContents());
		noticeBoardEntity.setViews(0);
		noticeBoardEntity.setLikeCount(0);
		noticeBoardEntity.setNickname(noticeBoardDTO.getNickname());
		noticeBoardEntity.setLoginid(noticeBoardDTO.getLoginid());
		noticeBoardEntity.setUser(userEntity);
		noticeBoardEntity.setFileAttached(1); // 파일 있음.
    	
    	return noticeBoardEntity;
	}

	public static NoticeBoardEntity toUpdateEntity(NoticeBoardDTO noticeBoardDTO, UserEntity userEntity) { 
		NoticeBoardEntity noticeBoardEntity = new NoticeBoardEntity();
		noticeBoardEntity.setSeq(noticeBoardDTO.getSeq());
		noticeBoardEntity.setTag(noticeBoardDTO.getTag());
		noticeBoardEntity.setTitle(noticeBoardDTO.getTitle());
		noticeBoardEntity.setTag(noticeBoardDTO.getTag());
		noticeBoardEntity.setContents(noticeBoardDTO.getContents());
		noticeBoardEntity.setViews(noticeBoardDTO.getViews());//
		noticeBoardEntity.setLikeCount(noticeBoardDTO.getLikeCount());//
		noticeBoardEntity.setNickname(noticeBoardDTO.getNickname());
		noticeBoardEntity.setLoginid(noticeBoardDTO.getLoginid());
		noticeBoardEntity.setFileAttached(noticeBoardDTO.getFileAttached());
		noticeBoardEntity.setUser(userEntity);
    	
    	return noticeBoardEntity;
	}



	
}
