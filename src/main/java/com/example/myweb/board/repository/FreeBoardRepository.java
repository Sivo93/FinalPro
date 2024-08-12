package com.example.myweb.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.myweb.board.dto.FreeBoardDTO;
import com.example.myweb.board.entity.FreeBoardEntity;

public interface FreeBoardRepository extends JpaRepository<FreeBoardEntity, Long>{
	// update free_board_table set views=views+1 where seq=?
	@Modifying
	@Transactional
	@Query(value = "update FreeBoardEntity b set b.views=b.views+1 where b.seq=:seq")
	void incrementViews(@Param("seq") Long seq);

	
	// 추천 수가 높은 3개의 글 가져오는 쿼리
    @Query("SELECT f FROM FreeBoardEntity f ORDER BY f.likeCount DESC")
    List<FreeBoardEntity> findTop3ByOrderByLikeCountDesc();
    
    // 제목 검색 검색어를 포함한 게시글 찾기
    @Query("SELECT new com.example.myweb.board.dto.FreeBoardDTO(e.seq, e.tag, e.title, e.createdTime, e.views, e.likeCount, e.nickname) " +
            "FROM FreeBoardEntity e WHERE e.title LIKE %:title%")
     Page<FreeBoardDTO> findByTitleContaining(@Param("title") String title, Pageable pageable);

 }
