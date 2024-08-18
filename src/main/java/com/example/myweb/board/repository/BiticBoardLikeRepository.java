package com.example.myweb.board.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.myweb.board.entity.BiticBoardEntity;
import com.example.myweb.board.entity.BiticBoardLikeEntity;
import com.example.myweb.user.entity.UserEntity;

public interface BiticBoardLikeRepository extends JpaRepository<BiticBoardLikeEntity, Long> {
	Optional<BiticBoardLikeEntity> findByUserAndBiticBoardEntity(UserEntity user, BiticBoardEntity biticBoardEntity);
    void deleteByUserAndBiticBoardEntity(UserEntity user, BiticBoardEntity biticBoardEntity);
}
