package com.example.pattern.controller;

import com.example.pattern.entity.User;
import com.example.pattern.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    // 跳转登录页面
    @GetMapping("/login")
    public String toLogin() {
        return "login"; // 对应templates/login.html
    }

    // 处理登录请求
    @PostMapping("/login")
    public String login(@RequestParam String username,
            @RequestParam String password,
            Model model,
            HttpSession session) {
        // 1. 查询用户
        User user = userRepository.findByUsername(username)
                .orElse(null);
        // 2. 验证用户
        if (user == null) {
            model.addAttribute("error", "用户名不存在");
            return "login";
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            model.addAttribute("error", "账号已禁用");
            return "login";
        }
        // 3. 验证密码（简单明文对比，实际应加密）
        if (!password.equals(user.getPassword())) {
            model.addAttribute("error", "密码错误");
            return "login";
        }
        // 4. 登录成功
        session.setAttribute("user", user);
        return "redirect:/gallery"; // 跳转到画廊页
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}