package com.example.myweb.board.entity;

import com.example.myweb.board.dto.NoticeBoardCommentDTO;
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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "notice_board_comment_table")
public class NoticeBoardCommentEntity extends BaseBoardEntity {
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
	@JoinColumn(name = "noticeBoard_seq")
	private NoticeBoardEntity noticeBoardEntity;

	public static NoticeBoardCommentEntity toSaveEntity(NoticeBoardCommentDTO noticeBoardCommentDTO,
			NoticeBoardEntity noticeBoardEntity, UserEntity userEntity) {
		NoticeBoardCommentEntity noticeBoardCommentEntity = new NoticeBoardCommentEntity();
		noticeBoardCommentEntity.setCommentContentes(noticeBoardCommentDTO.getCommentContents());
		noticeBoardCommentEntity.setNoticeBoardEntity(noticeBoardEntity);
		noticeBoardCommentEntity.setUser(userEntity);

		return noticeBoardCommentEntity;
	}
}