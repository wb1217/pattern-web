package com.example.pattern.controller;

import com.example.pattern.entity.Pattern;
import com.example.pattern.service.PatternService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class PatternController {

    @Autowired
    private PatternService patternService;

    // 访问根路径时，跳转到gallery (如果已登录)
    @GetMapping("/")
    public String index() {
        return "redirect:/gallery";
    }

    @GetMapping("/gallery")
    public String gallery(Model model) {
        return "gallery"; // 对应templates/gallery.html
    }

    @GetMapping("/api/patterns")
    @ResponseBody
    public List<Pattern> searchPatterns(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String form,
            @RequestParam(required = false) String style,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String theme,
            @RequestParam(required = false) String series,
            @RequestParam(required = false, defaultValue = "false") boolean onlyFavorites,
            jakarta.servlet.http.HttpSession session) {

        Long userId = null;
        com.example.pattern.entity.User user = (com.example.pattern.entity.User) session.getAttribute("user");
        if (user != null) {
            userId = user.getId();
        } else if (onlyFavorites) {
            return List.of(); // Not logged in, can't show favorites
        }

        return patternService.searchPatterns(keyword, form, style, color, theme, series, userId, onlyFavorites);
    }

    @org.springframework.web.bind.annotation.PostMapping("/api/favorite/toggle")
    @ResponseBody
    public java.util.Map<String, Object> toggleFavorite(@RequestParam Long patternId,
            jakarta.servlet.http.HttpSession session) {
        com.example.pattern.entity.User user = (com.example.pattern.entity.User) session.getAttribute("user");
        if (user == null) {
            return java.util.Map.of("success", false, "message", "未登录");
        }
        boolean isFavorited = patternService.toggleFavorite(user.getId(), patternId);
        return java.util.Map.of("success", true, "favorited", isFavorited);
    }

    @GetMapping("/api/favorites/ids")
    @ResponseBody
    public List<Long> getFavoriteIds(jakarta.servlet.http.HttpSession session) {
        com.example.pattern.entity.User user = (com.example.pattern.entity.User) session.getAttribute("user");
        if (user == null) {
            return List.of();
        }
        return patternService.getFavoritedPatternIds(user.getId());
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/api/favorites/clear-all")
    @ResponseBody
    public java.util.Map<String, Object> clearAllFavorites(jakarta.servlet.http.HttpSession session) {
        com.example.pattern.entity.User user = (com.example.pattern.entity.User) session.getAttribute("user");
        if (user == null) {
            return java.util.Map.of("success", false, "message", "未登录");
        }
        int count = patternService.clearAllFavorites(user.getId());
        return java.util.Map.of("success", true, "count", count);
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/api/favorites/batch")
    @ResponseBody
    public java.util.Map<String, Object> batchUnfavorite(
            @org.springframework.web.bind.annotation.RequestBody List<Long> patternIds,
            jakarta.servlet.http.HttpSession session) {
        com.example.pattern.entity.User user = (com.example.pattern.entity.User) session.getAttribute("user");
        if (user == null) {
            return java.util.Map.of("success", false, "message", "未登录");
        }
        patternService.batchUnfavorite(user.getId(), patternIds);
        return java.util.Map.of("success", true, "count", patternIds.size());
    }
}