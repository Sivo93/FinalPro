package com.example.myweb.board.entity;

import java.util.ArrayList;
import java.util.List;

import com.example.myweb.board.dto.FreeBoardDTO;

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
@Table(name = "freeBoard_table")
public class FreeBoardEntity extends BaseBoardEntity{
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

    @Column(nullable = false)
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
    
    @OneToMany(mappedBy = "freeBoardEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FreeBoardFileEntity> freeBoardFileEntityList = new ArrayList<>();

    @OneToMany(mappedBy = "freeBoardEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FreeBoardCommentEntity> freeBoardCommentEntityList = new ArrayList<>();
  
    public static FreeBoardEntity toSaveEntity(FreeBoardDTO freeBoardDTO, UserEntity userEntity) { // 파일이 없는 경우 호출하는 save
    	FreeBoardEntity freeBoardEntity = new FreeBoardEntity();
    	freeBoardEntity.setTag(freeBoardDTO.getTag());
    	freeBoardEntity.setTitle(freeBoardDTO.getTitle());
    	freeBoardEntity.setTag(freeBoardDTO.getTag());
    	freeBoardEntity.setContents(freeBoardDTO.getContents());
    	freeBoardEntity.setViews(0);
    	freeBoardEntity.setLikeCount(0);
    	freeBoardEntity.setNickname(freeBoardDTO.getNickname());
    	freeBoardEntity.setLoginid(freeBoardDTO.getLoginid());
    	freeBoardEntity.setUser(userEntity);
    	freeBoardEntity.setFileAttached(0); // 파일 없음.
    	
    	return freeBoardEntity;
    }

    public static FreeBoardEntity toSaveFileEntity(FreeBoardDTO freeBoardDTO, UserEntity userEntity) {
		FreeBoardEntity freeBoardEntity = new FreeBoardEntity();
    	freeBoardEntity.setTag(freeBoardDTO.getTag());
    	freeBoardEntity.setTitle(freeBoardDTO.getTitle());
    	freeBoardEntity.setTag(freeBoardDTO.getTag());
    	freeBoardEntity.setContents(freeBoardDTO.getContents());
    	freeBoardEntity.setViews(0);
    	freeBoardEntity.setLikeCount(0);
    	freeBoardEntity.setNickname(freeBoardDTO.getNickname());
    	freeBoardEntity.setLoginid(freeBoardDTO.getLoginid());
    	freeBoardEntity.setUser(userEntity);
    	freeBoardEntity.setFileAttached(1); // 파일 있음.
    	
    	return freeBoardEntity;
	}

	public static FreeBoardEntity toUpdateEntity(FreeBoardDTO freeBoardDTO, UserEntity userEntity) { 
		FreeBoardEntity freeBoardEntity = new FreeBoardEntity();
		freeBoardEntity.setSeq(freeBoardDTO.getSeq());
    	freeBoardEntity.setTag(freeBoardDTO.getTag());
    	freeBoardEntity.setTitle(freeBoardDTO.getTitle());
    	freeBoardEntity.setTag(freeBoardDTO.getTag());
    	freeBoardEntity.setContents(freeBoardDTO.getContents());
    	freeBoardEntity.setViews(freeBoardDTO.getViews());//
    	freeBoardEntity.setLikeCount(freeBoardDTO.getLikeCount());//
    	freeBoardEntity.setNickname(freeBoardDTO.getNickname());
    	freeBoardEntity.setLoginid(freeBoardDTO.getLoginid());
    	freeBoardEntity.setFileAttached(freeBoardDTO.getFileAttached());
    	freeBoardEntity.setUser(userEntity);
    	
    	return freeBoardEntity;
	}



	
}
