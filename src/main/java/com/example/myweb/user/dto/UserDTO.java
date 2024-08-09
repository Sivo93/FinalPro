package com.example.myweb.user.dto;

import java.util.Set;
import com.example.myweb.board.entity.FreeBoardLikeEntity;
import com.example.myweb.user.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDTO {
    private Long seq;
    private String loginid;
    private String pw;
    private String name;
    private String nickname;
    private String address;
    private String email;
    private String tel;
    private String role;
    @JsonIgnore
    private Set<FreeBoardLikeEntity> likes; // 추가

    public static UserDTO toUserDTO(UserEntity userEntity) {
        UserDTO userDTO = new UserDTO();
        userDTO.setSeq(userEntity.getSeq());
        userDTO.setLoginid(userEntity.getLoginid());
        userDTO.setPw(userEntity.getPw());
        userDTO.setName(userEntity.getName());
        userDTO.setNickname(userEntity.getNickname());
        userDTO.setAddress(userEntity.getAddress());
        userDTO.setEmail(userEntity.getEmail());
        userDTO.setTel(userEntity.getTel());
        userDTO.setRole(userEntity.getRole());
        userDTO.setLikes(userEntity.getLikes()); // 추가

        return userDTO;
    }
}
