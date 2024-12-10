package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许所有路径的跨域请求
                .allowedOrigins("*") // 允许所有域名的跨域请求
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许所有HTTP方法
                .allowCredentials(false); // 允许携带cookie
    }
}