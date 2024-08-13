package com.example.myweb.board.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.myweb.board.dto.BiticBoardDTO;

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
@Table(name = "biticBoard_table")
public class BiticBoardEntity extends BaseBoardEntity{
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
    
    @OneToMany(mappedBy = "biticBoardEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BiticBoardFileEntity> biticBoardFileEntityList = new ArrayList<>();

    @OneToMany(mappedBy = "biticBoardEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BiticBoardCommentEntity> biticBoardCommentEntityList = new ArrayList<>();
    
    @OneToMany(mappedBy = "biticBoardEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BiticBoardLikeEntity> likes = new HashSet<>();
  
    // Helper methods to manage the relationship
    public void addFile(BiticBoardFileEntity file) {
    	biticBoardFileEntityList.add(file);
        file.setBiticBoardEntity(this);
    }

    public void removeFile(BiticBoardFileEntity file) {
        biticBoardFileEntityList.remove(file);
        file.setBiticBoardEntity(null);
    }

    public void addComment(BiticBoardCommentEntity comment) {
        biticBoardCommentEntityList.add(comment);
        comment.setBiticBoardEntity(this);
    }

    public void removeComment(BiticBoardFileEntity comment) {
        biticBoardCommentEntityList.remove(comment);
        comment.setBiticBoardEntity(null);
    }

    public void addLike(BiticBoardLikeEntity like) {
        likes.add(like);
        like.setBiticBoardEntity(this);
    }
    
    public void removeLike(BiticBoardLikeEntity like) {
        likes.remove(like);
        like.setBiticBoardEntity(null);
    }
    
    public static BiticBoardEntity toSaveEntity(BiticBoardDTO biticBoardDTO, UserEntity userEntity) { // 파일이 없는 경우 호출하는 save
    	BiticBoardEntity biticBoardEntity = new BiticBoardEntity();
    	biticBoardEntity.setTag(biticBoardDTO.getTag());
    	biticBoardEntity.setTitle(biticBoardDTO.getTitle());
    	biticBoardEntity.setTag(biticBoardDTO.getTag());
    	biticBoardEntity.setContents(biticBoardDTO.getContents());
    	biticBoardEntity.setViews(0);
    	biticBoardEntity.setLikeCount(0);
    	biticBoardEntity.setNickname(biticBoardDTO.getNickname());
    	biticBoardEntity.setLoginid(biticBoardDTO.getLoginid());
    	biticBoardEntity.setUser(userEntity);
    	biticBoardEntity.setFileAttached(0); // 파일 없음.
    	
    	return biticBoardEntity;
    }

    public static BiticBoardEntity toSaveFileEntity(BiticBoardDTO biticBoardDTO, UserEntity userEntity) {
		BiticBoardEntity biticBoardEntity = new BiticBoardEntity();
		biticBoardEntity.setTag(biticBoardDTO.getTag());
		biticBoardEntity.setTitle(biticBoardDTO.getTitle());
		biticBoardEntity.setTag(biticBoardDTO.getTag());
		biticBoardEntity.setContents(biticBoardDTO.getContents());
		biticBoardEntity.setViews(0);
		biticBoardEntity.setLikeCount(0);
		biticBoardEntity.setNickname(biticBoardDTO.getNickname());
		biticBoardEntity.setLoginid(biticBoardDTO.getLoginid());
		biticBoardEntity.setUser(userEntity);
		biticBoardEntity.setFileAttached(1); // 파일 있음.
    	
    	return biticBoardEntity;
	}

	public static BiticBoardEntity toUpdateEntity(BiticBoardDTO biticBoardDTO, UserEntity userEntity) { 
		BiticBoardEntity biticBoardEntity = new BiticBoardEntity();
		biticBoardEntity.setSeq(biticBoardDTO.getSeq());
		biticBoardEntity.setTag(biticBoardDTO.getTag());
		biticBoardEntity.setTitle(biticBoardDTO.getTitle());
		biticBoardEntity.setTag(biticBoardDTO.getTag());
		biticBoardEntity.setContents(biticBoardDTO.getContents());
		biticBoardEntity.setViews(biticBoardDTO.getViews());//
		biticBoardEntity.setLikeCount(biticBoardDTO.getLikeCount());//
		biticBoardEntity.setNickname(biticBoardDTO.getNickname());
		biticBoardEntity.setLoginid(biticBoardDTO.getLoginid());
		biticBoardEntity.setFileAttached(biticBoardDTO.getFileAttached());
		biticBoardEntity.setUser(userEntity);
    	
    	return biticBoardEntity;
	}



	
}
