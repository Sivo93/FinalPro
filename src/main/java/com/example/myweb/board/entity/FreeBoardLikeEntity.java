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
@Table(name = "like_table", uniqueConstraints = {@UniqueConstraint(columnNames = {"loginid", "freeboard_seq"})})
public class FreeBoardLikeEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loginid")
    private UserEntity user;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "freeboard_seq")
    private FreeBoardEntity freeBoardEntity;
}
