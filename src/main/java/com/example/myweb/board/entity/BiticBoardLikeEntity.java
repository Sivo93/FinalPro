package com.example.myweb.board.entity;

import com.example.myweb.user.entity.UserEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "bitic_like_table", uniqueConstraints = {@UniqueConstraint(columnNames = {"loginid", "biticboard_seq"})})
public class BiticBoardLikeEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loginid")
    private UserEntity user;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "biticboard_seq")
    private BiticBoardEntity biticBoardEntity;
}
