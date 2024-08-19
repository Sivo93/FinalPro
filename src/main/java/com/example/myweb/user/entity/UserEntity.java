package com.example.myweb.user.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.example.myweb.board.entity.BiticBoardCommentEntity;
import com.example.myweb.board.entity.BiticBoardEntity;
import com.example.myweb.board.entity.BiticBoardLikeEntity;
import com.example.myweb.board.entity.FreeBoardCommentEntity;
import com.example.myweb.board.entity.FreeBoardEntity;
import com.example.myweb.board.entity.FreeBoardLikeEntity;
import com.example.myweb.board.entity.NoticeBoardCommentEntity;
import com.example.myweb.board.entity.NoticeBoardEntity;
import com.example.myweb.board.entity.NoticeBoardLikeEntity;
import com.example.myweb.user.dto.UserDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "user_table")
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long seq;

	@Column(unique = true, nullable = false)
	private String loginid;

	@Column(nullable = false)
	private String pw;

	@Column(nullable = false)
	private String name;

	@Column(unique = true, nullable = false)
	private String nickname;

	@Column(nullable = false)
	private String address;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String tel;

	@Column(nullable = true)
	private String role;
	
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<FreeBoardLikeEntity> freelikes;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<NoticeBoardLikeEntity> noticelikes;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<BiticBoardLikeEntity> biticlikes;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<FreeBoardEntity> freeBoards;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<NoticeBoardEntity> noticeBoards;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<BiticBoardEntity> biticBoards;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<FreeBoardCommentEntity> freeComments;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<NoticeBoardCommentEntity> noticeComments;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<BiticBoardCommentEntity> biticComments;


    public static UserEntity toUserEntity(UserDTO userDTO) {
        UserEntity userEntity = new UserEntity();
        userEntity.setLoginid(userDTO.getLoginid());
        userEntity.setPw(userDTO.getPw());
        userEntity.setName(userDTO.getName());
        userEntity.setNickname(userDTO.getNickname());
        userEntity.setAddress(userDTO.getAddress());
        userEntity.setEmail(userDTO.getEmail());
        userEntity.setTel(userDTO.getTel());
        userEntity.setRole(userDTO.getRole());
        return userEntity;
    }

    public static UserEntity toUpdateUserEntity(UserDTO userDTO) {
        UserEntity userEntity = new UserEntity();
        userEntity.setSeq(userDTO.getSeq());
        userEntity.setLoginid(userDTO.getLoginid());
        userEntity.setPw(userDTO.getPw());
        userEntity.setName(userDTO.getName());
        userEntity.setNickname(userDTO.getNickname());
        userEntity.setAddress(userDTO.getAddress());
        userEntity.setEmail(userDTO.getEmail());
        userEntity.setTel(userDTO.getTel());
        userEntity.setRole(userDTO.getRole());
        // Do not set likes during update to avoid changes
        return userEntity;
    }

	public void setResetToken(String token) {
		// TODO Auto-generated method stub
		
	}

}