package com.example.myweb.board.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.myweb.board.entity.FreeBoardEntity;
import com.example.myweb.board.entity.FreeBoardLikeEntity;
import com.example.myweb.user.entity.UserEntity;

public interface FreeBoardLikeRepository extends JpaRepository<FreeBoardLikeEntity, Long> {
	Optional<FreeBoardLikeEntity> findByUserAndFreeBoardEntity(UserEntity user, FreeBoardEntity freeBoardEntity);
    void deleteByUserAndFreeBoardEntity(UserEntity user, FreeBoardEntity freeBoardEntity);
}
