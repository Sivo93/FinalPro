package com.example.myweb.user.service;

import com.example.myweb.user.entity.UserEntity;
import com.example.myweb.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserEntity> findAllUsers() {
        return userRepository.findAll();
    }

    public UserEntity findUserByLoginid(String loginid) {
        return userRepository.findByLoginid(loginid);
    }

    public UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserEntity findUserByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    public void saveUser(UserEntity user) {
        userRepository.save(user);
    }
}
