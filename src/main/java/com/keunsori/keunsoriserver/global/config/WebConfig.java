package com.keunsori.keunsoriserver.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                //.allowedMethods("GET","POST","PUT","DELETE", "OPTIONS")
                .allowedMethods("*")
                //.allowedHeaders("Authorization", "Content-Type")
                .allowedHeaders("*")
                .exposedHeaders("Refresh-Token")
                //.allowCredentials("true")->도메인 정해지면 활성화
                .maxAge(3600);
    }
}
