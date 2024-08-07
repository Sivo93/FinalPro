package com.example.myweb.user.repository;

import com.example.myweb.user.entity.UserEntity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByLoginid(String loginid);
    UserEntity findByEmail(String email);
    UserEntity findByNickname(String nickname);
	Optional<UserEntity> findByLoginidAndNickname(String loginid, String nickname);
}
