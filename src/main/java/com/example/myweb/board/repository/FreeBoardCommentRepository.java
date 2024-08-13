package com.example.myweb.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.myweb.board.entity.FreeBoardCommentEntity;
import com.example.myweb.board.entity.FreeBoardEntity;

public interface FreeBoardCommentRepository extends JpaRepository<FreeBoardCommentEntity, Long>{
	// select * from free_board_comment_table where free_board_seq=? order by seq desc;
	List<FreeBoardCommentEntity> findAllByFreeBoardEntityOrderBySeqDesc(FreeBoardEntity freeBoardEntity);
}