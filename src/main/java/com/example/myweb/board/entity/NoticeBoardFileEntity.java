package com.example.myweb.board.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "notice_board_file_table")
public class NoticeBoardFileEntity extends BaseBoardEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long seq;
	
	@Column
	private String originalFileName;
	
	@Column
	private String storedFileName;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "noticeboard_seq")
	private NoticeBoardEntity noticeBoardEntity;
	
	public static NoticeBoardFileEntity toNoticeBoardFileEntity(NoticeBoardEntity noticeBoardEntity, String originalFileName, String storedFileName) {
		NoticeBoardFileEntity noticeBoardFileEntity = new NoticeBoardFileEntity();
		noticeBoardFileEntity.setOriginalFileName(originalFileName);
		noticeBoardFileEntity.setStoredFileName(storedFileName);
		noticeBoardFileEntity.setNoticeBoardEntity(noticeBoardEntity);
		
		return noticeBoardFileEntity;
	}
}





















