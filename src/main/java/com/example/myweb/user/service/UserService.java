package com.example.myweb.user.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.myweb.user.dto.UserDTO;
import com.example.myweb.user.entity.UserEntity;
import com.example.myweb.user.repository.UserRepository;
import com.example.myweb.user.security.JwtUtil;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	@Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;


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
//	public UserDTO findBySeq(Long seq) {
//		Optional<UserEntity> optionalUserEntity = userRepository.findById(seq);
//		return optionalUserEntity.map(UserDTO::toUserDTO).orElse(null);
//	}
	

//	public UserDTO updateForm(String myLoginid) {
//		Optional<UserEntity> optionalUserEntity = userRepository.findByLoginid(myLoginid);
//		if (optionalUserEntity.isPresent()) {
//			return UserDTO.toUserDTO(optionalUserEntity.get());
//		} else {
//			return null;
//		}
//	}

	public void update(UserDTO userDTO) {
		UserEntity userEntity = UserEntity.toUpdateUserEntity(userDTO);
		encodePassword(userEntity);
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

//	public boolean emailCheck(String email) {
//		return userRepository.findByEmail(email).isEmpty();
//	}
//
//	public boolean loginidCheck(String loginid) {
//		return userRepository.findByLoginid(loginid).isEmpty();
//	}
//
//	public boolean nicknameCheck(String nickname) {
//		return userRepository.findByNickname(nickname).isEmpty();
//	}
	// 사용자 등록
	public void registerUser(UserDTO userDTO) {
		UserEntity userEntity = UserEntity.toUserEntity(userDTO);
		userEntity.setPw(passwordEncoder.encode(userEntity.getPw()));
		userRepository.save(userEntity);
	}

	// 사용자 정보 업데이트
	public void updateUser(UserDTO userDTO) {
		UserEntity userEntity = UserEntity.toUpdateUserEntity(userDTO);
		userEntity.setPw(passwordEncoder.encode(userEntity.getPw()));
		userRepository.save(userEntity);
	}
	
	private void encodePassword(UserEntity userEntity) {
		userEntity.setPw(passwordEncoder.encode(userEntity.getPw()));
	}

	// Spring Security에서 사용되는 사용자 인증 메서드
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity user = userRepository.findByLoginid(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		return new org.springframework.security.core.userdetails.User(user.getLoginid(), user.getPw(),
				new ArrayList<>());
	}

	// 사용자 로그인 인증 메서드
	public boolean authenticate(String username, String rawPassword) {
		UserEntity user = userRepository.findByLoginid(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		return passwordEncoder.matches(rawPassword, user.getPw());
	}

	public String generateToken(String username) {
		return jwtUtil.generateToken(username);
	}
	
	public UserEntity findByLoginid(String loginid) {
	    return userRepository.findByLoginid(loginid)
	            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}
	
	public UserEntity getAuthenticatedUser(String loginid, String nickname) {
		System.out.println("Login ID: " + loginid);
	    System.out.println("Nickname: " + nickname);
        return userRepository.findByLoginidAndNickname(loginid, nickname)
                .orElseThrow(() -> new IllegalArgumentException("User not found with loginid " + loginid + " and nickname " + nickname));
    }
	
	public UserEntity findByLoginidAndNickname(String loginid, String nickname) {
        return userRepository.findByLoginidAndNickname(loginid, nickname)
                .orElseThrow(() -> new IllegalArgumentException("User not found with loginid " + loginid + " and nickname " + nickname));
    }

}
