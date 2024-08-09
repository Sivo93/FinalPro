package com.example.myweb.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.myweb.user.dto.UserDTO;
import com.example.myweb.user.entity.UserEntity;
import com.example.myweb.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public void save(UserDTO userDTO) {
		// 1. dto -> entity 변환

		// 2. repository의 save 메서드 호출
		UserEntity userEntity = UserEntity.toUserEntity(userDTO);
		userRepository.save(userEntity);
		System.out.println("회원가입 성공");

		// repository의 save 메서드 호출 (조건. entity객체를 넘겨줘야 함)
	}

	public UserDTO login(UserDTO userDTO) {
		// 1. 회원이 입력한 이메일로 db에서 조회를 함
		// 2. db에서 조회한 비밀번호와 사용자가 입력한 비밀번호가 일치하는지 판단
		Optional<UserEntity> byLoginid = userRepository.findByLoginid(userDTO.getLoginid());
		if (byLoginid.isPresent()) {
			// 조회 결과가 있다(해당 아이디를 가진 회원 정보가 있다)
			UserEntity userEntity = byLoginid.get();
			if (userEntity.getPw().equals(userDTO.getPw())) {
				// 비밀번호 일치
				// entity -> dto 변환 후 리턴
				UserDTO dto = UserDTO.toUserDTO(userEntity);
				System.out.println("로그인 성공!");
				return dto;
			} else {
				// 비밀번호 불일치(로그인실패)
				System.out.println("비밀번호 틀림!");
				return null;
			}
		} else {
			// 조회 결과가 없다(해당 아이디를 가진 회원 정보가 없다)
			System.out.println("아이디가 없습니다!");
			return null;
		}

	}

	public List<UserDTO> findAll() {
		List<UserEntity> userEntityList = userRepository.findAll();
		List<UserDTO> userDTOList = new ArrayList<>();

		for (UserEntity userEntity : userEntityList) {
			userDTOList.add(UserDTO.toUserDTO(userEntity));
		}

		return userDTOList;
	}

	public UserDTO findBySeq(Long seq) {
		Optional<UserEntity> optionalUserEntity = userRepository.findById(seq);
		if (optionalUserEntity.isPresent()) {
			System.out.println("조회 성공");
			return UserDTO.toUserDTO(optionalUserEntity.get());
		} else {
			System.out.println("조회 실패");
			return null;
		}
	}

    public UserDTO updateForm(String myLoginid) {
        Optional<UserEntity> optionalUserEntity = userRepository.findByLoginid(myLoginid);
        if (optionalUserEntity.isPresent()) {
            return UserDTO.toUserDTO(optionalUserEntity.get());
        } else {
            return null;
        }
    }

    public void update(UserDTO userDTO) {
        UserEntity userEntity = userRepository.findById(userDTO.getSeq())
            .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));


        userEntity.setPw(userDTO.getPw());
        userEntity.setName(userDTO.getName());
        userEntity.setNickname(userDTO.getNickname());
        userEntity.setAddress(userDTO.getAddress());
        userEntity.setEmail(userDTO.getEmail());
        userEntity.setTel(userDTO.getTel());
        userEntity.setRole(userDTO.getRole());

        userRepository.save(userEntity);
    }
    public void deleteBySeq(Long seq) {
        userRepository.deleteById(seq);
    }

	public String emailCheck(String email) {
		Optional<UserEntity> byEmail = userRepository.findByEmail(email);
		if (byEmail.isPresent()) {
			// 조회결과가 있다 -> 사용할 수 없다.
			return null;
		} else {
			// 조회결과가 없다 -> 사용할 수 있다.
			return "ok";
		}
	}

	public String loginidCheck(String loginid) {
		Optional<UserEntity> byLoginid = userRepository.findByLoginid(loginid);
		if (byLoginid.isPresent()) {
			// 조회결과가 있다 -> 사용할 수 없다.
			return null;
		} else {
			// 조회결과가 없다 -> 사용할 수 있다.
			return "ok";
		}
	}

	public String nicknameCheck(String nickname) {
		Optional<UserEntity> byNickname = userRepository.findByNickname(nickname);
		if (byNickname.isPresent()) {
			// 조회결과가 있다 -> 사용할 수 없다.
			return null;
		} else {
			// 조회결과가 없다 -> 사용할 수 있다.
			return "ok";
		}
	}
    public UserDTO findByLoginid(String loginid) {
        Optional<UserEntity> optionalUserEntity = userRepository.findByLoginid(loginid);
        return optionalUserEntity.map(UserDTO::toUserDTO).orElse(null);
    }

}
