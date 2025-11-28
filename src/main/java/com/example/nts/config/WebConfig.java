package com.example.nts.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Разрешает CORS для всех путей
                .allowedOriginPatterns("*")  // Разрешить доступ для всех доменов
                //.allowedOrigins("http://localhost:8088", "http://127.0.0.1:8088")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true);  // Разрешить учетные данные (cookies, headers)
    }
}
