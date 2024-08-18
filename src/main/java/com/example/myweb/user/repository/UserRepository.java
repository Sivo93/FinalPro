package com.example.myweb.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.myweb.user.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long>{

	// 이메일로 회원 정보 조회(select * from user_table where member_email=?)
	//Optional<UserEntity> findbyUserLogin_id(String login_id);
	Optional<UserEntity> findByLoginid(String loginid);
	Optional<UserEntity> findByNickname(String nickname);
	Optional<UserEntity> findByEmail(String email);
	Optional<UserEntity> findByLoginidAndNickname(String loginid, String nickname);
	Optional<UserEntity> findByNameAndEmail(String name, String email);
	Optional<UserEntity> findByLoginidAndEmail(String loginid, String email);


}