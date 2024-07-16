package com.example.myweb.board.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{
	private String resourcePath = "/upload/**"; // view 에서 접근할 경로
//	private String savePath = "file:///C:/springboot_img"; // 실제 파일 저장 경로
	private static final String SAVE_PATH = "classpath:/static/upload/"; // 프로젝트 내의 정적 리소스 경로
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler(resourcePath)
				.addResourceLocations(SAVE_PATH);
	}
	
	
}
