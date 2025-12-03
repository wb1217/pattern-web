package com.example.pattern.controller;

import com.example.pattern.entity.Pattern;
import com.example.pattern.entity.User;
import com.example.pattern.service.FileStorageService;
import com.example.pattern.service.PatternService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PatternService patternService;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * 检查用户是否为管理员
     */
    private ResponseEntity<?> checkAdminPermission(User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "未登录"));
        }
        if (!user.isAdmin()) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "您没有管理员权限"));
        }
        return null; // 权限检查通过
    }

    /**
     * 管理员页面
     */
    @GetMapping
    public String adminPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        // 检查是否为管理员
        if (!user.isAdmin()) {
            model.addAttribute("error", "您没有管理员权限，无法访问此页面");
            return "redirect:/gallery";
        }
        
        List<Pattern> patterns = patternService.findAll();
        model.addAttribute("patterns", patterns);
        model.addAttribute("user", user);
        return "admin";
    }

    /**
     * 添加纹理（带文件上传）
     */
    @PostMapping("/patterns")
    @ResponseBody
    public ResponseEntity<?> addPattern(
            @RequestParam("name") String name,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("form") String form,
            @RequestParam("style") String style,
            @RequestParam("color") String color,
            @RequestParam("theme") String theme,
            @RequestParam(value = "sort", required = false, defaultValue = "0") Integer sort,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            HttpSession session) {
        
        try {
            User user = (User) session.getAttribute("user");
            ResponseEntity<?> permissionCheck = checkAdminPermission(user);
            if (permissionCheck != null) {
                return permissionCheck;
            }

            Pattern pattern = new Pattern();
            pattern.setName(name);
            pattern.setCategory(category);
            pattern.setDescription(description);
            pattern.setForm(form);
            pattern.setStyle(style);
            pattern.setColor(color);
            pattern.setTheme(theme);
            pattern.setSort(sort);

            // 处理图片：优先使用上传的文件，否则使用URL
            if (imageFile != null && !imageFile.isEmpty()) {
                String savedPath = fileStorageService.saveFile(imageFile);
                pattern.setImageUrl(savedPath);
            } else if (imageUrl != null && !imageUrl.isEmpty()) {
                pattern.setImageUrl(imageUrl);
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "请上传图片或提供图片URL"));
            }

            Pattern savedPattern = patternService.save(pattern);
            return ResponseEntity.ok(Map.of("success", true, "message", "添加成功", "data", savedPattern));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "添加失败：" + e.getMessage()));
        }
    }

    /**
     * 更新纹理
     */
    @PutMapping("/patterns/{id}")
    @ResponseBody
    public ResponseEntity<?> updatePattern(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("form") String form,
            @RequestParam("style") String style,
            @RequestParam("color") String color,
            @RequestParam("theme") String theme,
            @RequestParam(value = "sort", required = false, defaultValue = "0") Integer sort,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            HttpSession session) {
        
        try {
            User user = (User) session.getAttribute("user");
            ResponseEntity<?> permissionCheck = checkAdminPermission(user);
            if (permissionCheck != null) {
                return permissionCheck;
            }

            Pattern pattern = patternService.findById(id)
                    .orElseThrow(() -> new RuntimeException("纹理不存在"));

            pattern.setName(name);
            pattern.setCategory(category);
            pattern.setDescription(description);
            pattern.setForm(form);
            pattern.setStyle(style);
            pattern.setColor(color);
            pattern.setTheme(theme);
            pattern.setSort(sort);

            // 处理图片更新
            if (imageFile != null && !imageFile.isEmpty()) {
                // 删除旧图片（如果是本地上传的）
                if (pattern.getImageUrl() != null && pattern.getImageUrl().startsWith("/uploads/")) {
                    fileStorageService.deleteFile(pattern.getImageUrl());
                }
                String savedPath = fileStorageService.saveFile(imageFile);
                pattern.setImageUrl(savedPath);
            } else if (imageUrl != null && !imageUrl.isEmpty()) {
                pattern.setImageUrl(imageUrl);
            }

            Pattern updatedPattern = patternService.save(pattern);
            return ResponseEntity.ok(Map.of("success", true, "message", "更新成功", "data", updatedPattern));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "更新失败：" + e.getMessage()));
        }
    }

    /**
     * 删除纹理
     */
    @DeleteMapping("/patterns/{id}")
    @ResponseBody
    public ResponseEntity<?> deletePattern(@PathVariable Long id, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            ResponseEntity<?> permissionCheck = checkAdminPermission(user);
            if (permissionCheck != null) {
                return permissionCheck;
            }

            Pattern pattern = patternService.findById(id)
                    .orElseThrow(() -> new RuntimeException("纹理不存在"));

            // 删除关联的图片文件
            if (pattern.getImageUrl() != null && pattern.getImageUrl().startsWith("/uploads/")) {
                fileStorageService.deleteFile(pattern.getImageUrl());
            }

            patternService.deleteById(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "删除成功"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "删除失败：" + e.getMessage()));
        }
    }

    /**
     * 获取单个纹理详情
     */
    @GetMapping("/patterns/{id}")
    @ResponseBody
    public ResponseEntity<?> getPattern(@PathVariable Long id) {
        try {
            Pattern pattern = patternService.findById(id)
                    .orElseThrow(() -> new RuntimeException("纹理不存在"));
            return ResponseEntity.ok(Map.of("success", true, "data", pattern));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
