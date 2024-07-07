package com.example.myweb.user.dto;

import com.example.myweb.user.entity.UserEntity;

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
	private String role; // UserRole Enum 타입으로 변경
	
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
		
		return userDTO;
	}

}
