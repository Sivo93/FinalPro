package com.example.myweb.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.myweb.board.entity.FreeBoardEntity;

public interface FreeBoardRepository extends JpaRepository<FreeBoardEntity, Long> {
	// update free_board_table set views=views+1 where seq=?
	@Modifying
	@Transactional
	@Query(value = "update FreeBoardEntity b set b.views=b.views+1 where b.seq=:seq")
	void incrementViews(@Param("seq") Long seq);

	@EntityGraph(attributePaths = "freeBoardFileEntityList")
	Page<FreeBoardEntity> findByTitleContaining(String keyword, Pageable pageable);
}
