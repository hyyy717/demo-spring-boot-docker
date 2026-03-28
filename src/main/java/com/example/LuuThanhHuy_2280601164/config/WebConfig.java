package com.example.LuuThanhHuy_2280601164.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ép hệ thống đọc file ảnh trực tiếp từ ổ cứng theo thời gian thực
        String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/images/";
        Path uploadPath = Paths.get(uploadDir);

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadPath.toAbsolutePath() + "/");
    }
}