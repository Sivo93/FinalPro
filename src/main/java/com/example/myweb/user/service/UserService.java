package com.example.myweb.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.myweb.user.dto.UserDTO;
import com.example.myweb.user.entity.UserEntity;
import com.example.myweb.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public void save(UserDTO userDTO) {
		String loginid = userDTO.getLoginid();
		String pw = userDTO.getPw();
		String name = userDTO.getName();
		String nickname = userDTO.getNickname();
		String address = userDTO.getAddress();
		String email = userDTO.getEmail();
		String tel = userDTO.getTel();
		String role = userDTO.getRole();

		UserEntity userEntity = new UserEntity();
		userEntity.setLoginid(loginid);
		userEntity.setPw(bCryptPasswordEncoder.encode(pw));
		userEntity.setName(name);
		userEntity.setNickname(nickname);
		userEntity.setAddress(address);
		userEntity.setEmail(email);
		userEntity.setTel(tel);
		userEntity.setRole(role);

		userRepository.save(userEntity);
		System.out.println("회원가입 성공");

		// repository의 save 메서드 호출 (조건. entity객체를 넘겨줘야 함)
	}


	public UserDTO login(UserDTO userDTO) {
		Optional<UserEntity> byLoginid = userRepository.findByLoginid(userDTO.getLoginid());
		if (byLoginid.isPresent()) {
			UserEntity userEntity = byLoginid.get();
			// 암호화된 비밀번호와 입력한 비밀번호 비교
			if (bCryptPasswordEncoder.matches(userDTO.getPw(), userEntity.getPw())) {
				UserDTO dto = UserDTO.toUserDTO(userEntity);
				System.out.println("로그인 성공!");
				return dto;
			} else {
				System.out.println("비밀번호 틀림!");
				return null;
			}
		} else {
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

//    public void update(UserDTO userDTO) {
//        UserEntity userEntity = userRepository.findById(userDTO.getSeq())
//            .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
//
//
//        userEntity.setPw(userDTO.getPw());
//        userEntity.setName(userDTO.getName());
//        userEntity.setNickname(userDTO.getNickname());
//        userEntity.setAddress(userDTO.getAddress());
//        userEntity.setEmail(userDTO.getEmail());
//        userEntity.setTel(userDTO.getTel());
//        userEntity.setRole(userDTO.getRole());
//
//        userRepository.save(userEntity);
//    }
//    public void deleteBySeq(Long seq) {
//        userRepository.deleteById(seq);
//    }

	public void update(UserDTO userDTO) {
		Optional<UserEntity> optionalUserEntity = userRepository.findById(userDTO.getSeq());
		if (optionalUserEntity.isPresent()) {
			UserEntity userEntity = optionalUserEntity.get();
			userEntity.setLoginid(userDTO.getLoginid());

			if (!bCryptPasswordEncoder.matches(userDTO.getPw(), userEntity.getPw())) {
				userEntity.setPw(bCryptPasswordEncoder.encode(userDTO.getPw()));
			}

			userEntity.setName(userDTO.getName());
			userEntity.setNickname(userDTO.getNickname());
			userEntity.setAddress(userDTO.getAddress());
			userEntity.setEmail(userDTO.getEmail());
			userEntity.setTel(userDTO.getTel());
			userEntity.setRole(userDTO.getRole());

			userRepository.save(userEntity);
			System.out.println("업데이트 성공");
		} else {
			System.out.println("업데이트 실패: 사용자 없음");
		}
	}

	public void deleteBySeq(Long seq) {
		 // 먼저 관련된 채팅 메시지를 삭제합니다.
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

	public UserDTO findByNameAndEmail(String name, String email) {
		Optional<UserEntity> optionalUserEntity = userRepository.findByNameAndEmail(name, email);
		return optionalUserEntity.map(UserDTO::toUserDTO).orElse(null);
	}

	public UserDTO findByLoginidAndEmail(String loginid, String email) {
		Optional<UserEntity> optionalUserEntity = userRepository.findByLoginidAndEmail(loginid, email);
		return optionalUserEntity.map(UserDTO::toUserDTO).orElse(null);
	}

	public boolean requestPasswordReset(String loginid, String email) {
		Optional<UserEntity> userOptional = userRepository.findByLoginidAndEmail(loginid, email);
		return userOptional.isPresent();
	}

	public boolean checkUserExists(String loginid, String email) {
        // 해당 아이디와 이메일로 사용자 존재 여부 확인
        return userRepository.findByLoginidAndEmail(loginid, email).isPresent();
    }

    public boolean updatePassword(String loginid, String email, String newPassword) {
        Optional<UserEntity> optionalUser = userRepository.findByLoginidAndEmail(loginid, email);
        if (!optionalUser.isPresent()) {
            return false; // 사용자 없음
        }

        UserEntity user = optionalUser.get();
        
        // 비밀번호 암호화
        String encodedPassword = bCryptPasswordEncoder.encode(newPassword);
        user.setPw(encodedPassword);
        
        // 사용자 업데이트
        userRepository.save(user);
        
        return true; // 성공적으로 비밀번호가 업데이트됨
    }

}