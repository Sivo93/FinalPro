package com.example.myweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

// 웹 어플리케이션이 다른 도메인에서 리소스를 요청할 수 있도록 허용하는 보안 기능 (CORS)
// 예) example.com에서 로드된 웹 페이지가 api.example.com에서 데이터를 가져오려 할 때 두 도메인이 다르기 때문에 요청이 차단될 수 있음
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 자격 증명 허용 예) 쿠키
        config.addAllowedOriginPattern("*"); // 모든 도메인 허용 (패턴 사용 가능)
        config.addAllowedHeader("*"); // 모든 헤더 허용
        config.addAllowedMethod("*"); // 모든 http 메서드 허용 (GET, POST, PUT, DELETE 등)
        source.registerCorsConfiguration("/**", config); // 모든 경로에 대해 CORS 설정 적용
        return new CorsFilter(source); // 구성된 CORS 필터 반환
    }
}
