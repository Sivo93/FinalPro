package com.example.myweb.shop;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration("shopWebConfig")
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 이미지를 처리하는 핸들러
        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations("file:uploads/images/");

        // 기본적인 정적 리소스 핸들러
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}
