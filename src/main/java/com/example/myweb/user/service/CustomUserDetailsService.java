package com.example.myweb.user.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.myweb.user.dto.CustomUserDetails;
import com.example.myweb.user.entity.UserEntity;
import com.example.myweb.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	
	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String loginid) throws UsernameNotFoundException {
		
		UserEntity userData = userRepository.findByLoginid(loginid).get();
		
		if(userData != null) {
			return new CustomUserDetails(userData);
		}
		
		return null;
	}

}
