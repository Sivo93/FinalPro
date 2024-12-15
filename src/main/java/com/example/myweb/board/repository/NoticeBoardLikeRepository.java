package com.example.myweb.board.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.myweb.board.entity.NoticeBoardEntity;
import com.example.myweb.board.entity.NoticeBoardLikeEntity;
import com.example.myweb.user.entity.UserEntity;

public interface NoticeBoardLikeRepository extends JpaRepository<NoticeBoardLikeEntity, Long> {
	Optional<NoticeBoardLikeEntity> findByUserAndNoticeBoardEntity(UserEntity user, NoticeBoardEntity noticeBoardEntity);
    void deleteByUserAndNoticeBoardEntity(UserEntity user, NoticeBoardEntity noticeBoardEntity);
}
