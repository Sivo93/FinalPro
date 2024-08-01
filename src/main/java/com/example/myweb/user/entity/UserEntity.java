package com.example.myweb.user.entity;

import com.example.myweb.user.dto.UserDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(unique = true, nullable = false)
    private String loginid;

    @Column(nullable = false)
    private String pw;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String address;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String tel;

    @Column(nullable = true)
    private String signupType; // 변경된 부분

    public UserEntity() {
    }

    public UserEntity(String nickname) {
        this.nickname = nickname;
    }

    public static UserEntity toUserEntity(UserDTO userDTO) {
        UserEntity userEntity = new UserEntity();
        userEntity.setLoginid(userDTO.getLoginid());
        userEntity.setPw(userDTO.getPw());
        userEntity.setName(userDTO.getName());
        userEntity.setNickname(userDTO.getNickname());
        userEntity.setAddress(userDTO.getAddress());
        userEntity.setEmail(userDTO.getEmail());
        userEntity.setTel(userDTO.getTel());
        userEntity.setSignupType(userDTO.getSignupType()); // 변경된 부분

        return userEntity;
    }

    public static UserEntity toUpdateUserEntity(UserDTO userDTO) {
        UserEntity userEntity = new UserEntity();
        userEntity.setSeq(userDTO.getSeq());
        userEntity.setLoginid(userDTO.getLoginid());
        userEntity.setPw(userDTO.getPw());
        userEntity.setName(userDTO.getName());
        userEntity.setNickname(userDTO.getNickname());
        userEntity.setAddress(userDTO.getAddress());
        userEntity.setEmail(userDTO.getEmail());
        userEntity.setTel(userDTO.getTel());
        userEntity.setSignupType(userDTO.getSignupType()); // 변경된 부분

        return userEntity;
    }
}
