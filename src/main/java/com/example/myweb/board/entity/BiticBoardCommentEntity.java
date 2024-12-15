package com.example.myweb.board.entity;

import com.example.myweb.board.dto.BiticBoardCommentDTO;
import com.example.myweb.user.entity.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "bitic_board_comment_table")
public class BiticBoardCommentEntity extends BaseBoardEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long seq;

	@Column(nullable = false)
	private String commentContentes;

	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "loginid", referencedColumnName = "loginid"),
        @JoinColumn(name = "nickname", referencedColumnName = "nickname")
    })
    private UserEntity user;
//	board : comment = 1 : N
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "biticBoard_seq")
	private BiticBoardEntity biticBoardEntity;

	public static BiticBoardCommentEntity toSaveEntity(BiticBoardCommentDTO biticBoardCommentDTO,
			BiticBoardEntity biticBoardEntity, UserEntity userEntity) {
		BiticBoardCommentEntity biticBoardCommentEntity = new BiticBoardCommentEntity();
		biticBoardCommentEntity.setCommentContentes(biticBoardCommentDTO.getCommentContents());
		biticBoardCommentEntity.setBiticBoardEntity(biticBoardEntity);
		biticBoardCommentEntity.setUser(userEntity);

		return biticBoardCommentEntity;
	}
}
