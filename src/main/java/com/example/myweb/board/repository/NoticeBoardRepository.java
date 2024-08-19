package com.example.myweb.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.myweb.board.entity.NoticeBoardEntity;

public interface NoticeBoardRepository extends JpaRepository<NoticeBoardEntity, Long>{
	// update notice_board_table set views=views+1 where seq=?
	// update notice_board_table set views=views+1 where seq=?
	@Modifying
	@Transactional
	@Query(value = "update NoticeBoardEntity b set b.views=b.views+1 where b.seq=:seq")
	void incrementViews(@Param("seq") Long seq);
    // 태그로 필터링
    Page<NoticeBoardEntity> findByTag(String tag, Pageable pageable);

    // 제목으로 필터링
    Page<NoticeBoardEntity> findByTitleContaining(String title, Pageable pageable);

    // 태그와 제목으로 동시에 필터링
    Page<NoticeBoardEntity> findByTagAndTitleContaining(String tag, String title, Pageable pageable);

    // 추천 수가 높은 3개의 글을 가져오는 쿼리
    @Query("SELECT f FROM NoticeBoardEntity f ORDER BY f.likeCount DESC")
    List<NoticeBoardEntity> findTop3ByOrderByLikeCountDesc();
 }