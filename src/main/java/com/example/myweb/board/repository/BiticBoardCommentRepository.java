package com.example.myweb.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.myweb.board.entity.BiticBoardCommentEntity;
import com.example.myweb.board.entity.BiticBoardEntity;

public interface BiticBoardCommentRepository extends JpaRepository<BiticBoardCommentEntity, Long>{
	// select * from bitic_board_comment_table where bitic_board_seq=? order by seq desc;
	List<BiticBoardCommentEntity> findAllByBiticBoardEntityOrderBySeqDesc(BiticBoardEntity biticBoardEntity);
}