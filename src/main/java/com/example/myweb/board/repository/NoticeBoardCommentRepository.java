package com.example.myweb.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.myweb.board.entity.NoticeBoardCommentEntity;
import com.example.myweb.board.entity.NoticeBoardEntity;

public interface NoticeBoardCommentRepository extends JpaRepository<NoticeBoardCommentEntity, Long>{
	// select * from notice_board_comment_table where notice_board_seq=? order by seq desc;
	List<NoticeBoardCommentEntity> findAllByNoticeBoardEntityOrderBySeqDesc(NoticeBoardEntity noticeBoardEntity);
}