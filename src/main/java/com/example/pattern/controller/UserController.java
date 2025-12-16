package com.example.pattern.controller;

import com.example.pattern.entity.User;
import com.example.pattern.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/api/user/avatar")
    @ResponseBody
    public ResponseEntity<?> uploadAvatar(@RequestParam("avatar") MultipartFile file, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "未登录"));
        }

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "文件为空"));
        }

        try {
            // 确保目录存在
            String uploadDir = "uploads/avatars";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath);

            // 更新用户信息
            String avatarUrl = "/uploads/avatars/" + newFilename;
            user.setAvatar(avatarUrl);
            userRepository.save(user);

            // 更新 Session
            session.setAttribute("user", user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("avatarUrl", avatarUrl);
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "上传失败: " + e.getMessage()));
        }
    }
}
