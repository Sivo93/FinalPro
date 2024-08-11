package com.example.myweb.board.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.myweb.interceptor.LoginInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private static final String RESOURCE_PATH = "/upload/**";
//    private static final String SAVE_PATH = "classpath:/static/upload/";
    private static final String SAVE_PATH = "file:///C:/upload/"; // 실제 파일 저장 위치
    
    private final LoginInterceptor loginInterceptor;

    @Autowired
    public WebConfig(LoginInterceptor loginInterceptor) {
        this.loginInterceptor = loginInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/freeboard/**")
                .excludePathPatterns("/login", "/signup", "/css/**", "/js/**", "/freeboard/paging", "/freeboard/*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(RESOURCE_PATH)
                .addResourceLocations(SAVE_PATH);
    }
}
