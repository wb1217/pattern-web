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
            @RequestParam(required = false) String form,
            @RequestParam(required = false) String style,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String theme) {
        return patternService.searchPatterns(form, style, color, theme);
    }
}