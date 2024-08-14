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
@Table(name = "free_board_file_table")
public class FreeBoardFileEntity extends BaseBoardEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long seq;
	
	@Column
	private String originalFileName;
	
	@Column
	private String storedFileName;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "freeboard_seq")
	private FreeBoardEntity freeBoardEntity;
	
	public static FreeBoardFileEntity toFreeBoardFileEntity(FreeBoardEntity freeBoardEntity, String originalFileName, String storedFileName) {
		FreeBoardFileEntity freeBoardFileEntity = new FreeBoardFileEntity();
		freeBoardFileEntity.setOriginalFileName(originalFileName);
		freeBoardFileEntity.setStoredFileName(storedFileName);
		freeBoardFileEntity.setFreeBoardEntity(freeBoardEntity);
		
		return freeBoardFileEntity;
	}
}





















