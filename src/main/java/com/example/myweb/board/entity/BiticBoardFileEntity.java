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
@Table(name = "bitic_board_file_table")
public class BiticBoardFileEntity extends BaseBoardEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long seq;
	
	@Column
	private String originalFileName;
	
	@Column
	private String storedFileName;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "biticboard_seq")
	private BiticBoardEntity biticBoardEntity;
	
	public static BiticBoardFileEntity toBiticBoardFileEntity(BiticBoardEntity biticBoardEntity, String originalFileName, String storedFileName) {
		BiticBoardFileEntity biticBoardFileEntity = new BiticBoardFileEntity();
		biticBoardFileEntity.setOriginalFileName(originalFileName);
		biticBoardFileEntity.setStoredFileName(storedFileName);
		biticBoardFileEntity.setBiticBoardEntity(biticBoardEntity);
		
		return biticBoardFileEntity;
	}
}





















