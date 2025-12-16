package com.example.pattern.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Value("${file.upload-dir:uploads/patterns}")
    private String uploadDir;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/css/**", "/js/**", "/images/**", "/uploads/**", "/error");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置上传文件的访问路径
        String uploadPath = Paths.get(uploadDir).toAbsolutePath().toString();
        registry.addResourceHandler("/uploads/patterns/**")
                .addResourceLocations("file:" + uploadPath + "/");

        // 头像访问路径
        String avatarUploadPath = Paths.get("uploads/avatars").toAbsolutePath().toString();
        registry.addResourceHandler("/uploads/avatars/**")
                .addResourceLocations("file:" + avatarUploadPath + "/");
    }
}
