package com.example.pattern.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir:uploads/patterns}")
    private String uploadDir;

    /**
     * 初始化上传目录
     */
    public void init() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("无法创建上传目录！", e);
        }
    }

    /**
     * 保存上传的文件
     * 
     * @param file 上传的文件
     * @return 保存后的文件名（相对路径）
     */
    public String saveFile(MultipartFile file) {
        try {
            // 初始化目录
            init();

            // 获取原始文件名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                throw new RuntimeException("文件名不能为空");
            }

            // 生成唯一文件名：UUID + 原始扩展名
            String extension = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex > 0) {
                extension = originalFilename.substring(dotIndex);
            }
            String newFilename = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path targetPath = Paths.get(uploadDir).resolve(newFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 返回相对路径
            return "/uploads/patterns/" + newFilename;
        } catch (IOException e) {
            throw new RuntimeException("文件保存失败：" + e.getMessage(), e);
        }
    }

    /**
     * 删除文件
     * 
     * @param fileUrl 文件URL路径
     */
    public void deleteFile(String fileUrl) {
        try {
            if (fileUrl != null && fileUrl.startsWith("/uploads/patterns/")) {
                String filename = fileUrl.substring("/uploads/patterns/".length());
                Path filePath = Paths.get(uploadDir).resolve(filename);
                Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("文件删除失败：" + e.getMessage(), e);
        }
    }
}
