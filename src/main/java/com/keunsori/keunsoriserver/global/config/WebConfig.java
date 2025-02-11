package com.keunsori.keunsoriserver.global.config;

import static com.keunsori.keunsoriserver.global.constant.EnvironmentConstant.DEV_URL;
import static com.keunsori.keunsoriserver.global.constant.EnvironmentConstant.LOCAL_URL_1;
import static com.keunsori.keunsoriserver.global.constant.EnvironmentConstant.LOCAL_URL_2;
import static com.keunsori.keunsoriserver.global.constant.EnvironmentConstant.LOCAL_URL_3;
import static com.keunsori.keunsoriserver.global.constant.EnvironmentConstant.PROD_URL;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins(LOCAL_URL_1, LOCAL_URL_2, LOCAL_URL_3, DEV_URL, PROD_URL)
//                .allowedMethods("*")
//                .allowedHeaders("*")
//                .exposedHeaders("Refresh-Token")
//                .allowCredentials(true)
//                .maxAge(3600);
//    }
}
