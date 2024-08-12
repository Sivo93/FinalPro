package com.example.myweb.user.security;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	//AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
	private final AuthenticationConfiguration authenticationConfiguration;
	private final JWTUtil jwtUtil;
	
	//AuthenticationManager Bean 등록
	@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }
	
	@Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http
				.cors((cors) -> cors
						.configurationSource(new CorsConfigurationSource() {
							
							@Override
							public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

								CorsConfiguration configuration = new CorsConfiguration();

			                    configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
			                    configuration.setAllowedMethods(Collections.singletonList("*"));
			                    configuration.setAllowCredentials(true);
			                    configuration.setAllowedHeaders(Collections.singletonList("*"));
			                    configuration.setMaxAge(3600L);

								configuration.setExposedHeaders(Collections.singletonList("Authorization"));

			                    return configuration;
			                }
							
						}));
		
		//csrf disable
        http
                .csrf((auth) -> auth.disable());
        //로그인 disable
        http
        		.formLogin((auth) -> auth.disable());        
//        http
//        		.formLogin()
//        			.loginPage("/user/login").permitAll();
        
//        http
//        		.rememberMe()
//        			.key("uniqueAndSecret")
//        			.rememberMeParameter("rememberMe")
//        			.tokenValiditySeconds(86400);

		//http basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());
		
        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                		.requestMatchers("/upload/**").permitAll() // 파일 접근 경로 허용
                        .requestMatchers("/uploadImage").permitAll() // 파일 업로드 경로 허용
                		.requestMatchers("/**").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN") // 관리자는 관리자 페이지 접근 가능
                        .anyRequest().authenticated()); // 나머지 요청은 인증 필요
        
        http
        		.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
        
        http
        		.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);
        
       
        
		//세션 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        
        
		return http.build();
	}
	
}
