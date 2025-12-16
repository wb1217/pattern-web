package com.example.pattern.component;

import com.example.pattern.entity.Pattern;
import com.example.pattern.entity.User;
import com.example.pattern.repository.PatternRepository;
import com.example.pattern.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PatternRepository patternRepository;

        @Override
        public void run(String... args) throws Exception {
                // Initialize Users
                if (userRepository.count() == 0) {
                        // 创建管理员用户 (status = 1)
                        User admin = new User();
                        admin.setUsername("admin");
                        admin.setPassword("123456"); // In real app, use BCrypt
                        admin.setNickname("管理员");
                        admin.setStatus(User.STATUS_ADMIN); // status = 1
                        admin.setCreateTime(LocalDateTime.now());
                        admin.setUpdateTime(LocalDateTime.now());
                        userRepository.save(admin);
                        System.out.println("Initialized admin user: admin / 123456 (status=1)");

                        // 创建普通用户 (status = 2)
                        User normalUser = new User();
                        normalUser.setUsername("user");
                        normalUser.setPassword("123456");
                        normalUser.setNickname("普通用户");
                        normalUser.setStatus(User.STATUS_USER); // status = 2
                        normalUser.setCreateTime(LocalDateTime.now());
                        normalUser.setUpdateTime(LocalDateTime.now());
                        userRepository.save(normalUser);
                        System.out.println("Initialized normal user: user / 123456 (status=2)");
                }

                // Initialize Patterns
                if (patternRepository.count() == 0) {
                        // 二方连续
                        createPattern("Geometric Waves", "二方连续", "现代主义", "蓝", "几何图形",
                                        "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?auto=format&fit=crop&w=800&q=80");
                        createPattern("Classic Border", "二方连续", "复古风", "红", "纹样",
                                        "https://images.unsplash.com/photo-1579783902614-a3fb3927b6a5?auto=format&fit=crop&w=800&q=80");
                        createPattern("Wave Pattern", "二方连续", "新中式", "蓝", "纹理与材质",
                                        "https://images.unsplash.com/photo-1557672172-298e090bd0f1?auto=format&fit=crop&w=800&q=80");

                        // 四方连续
                        createPattern("Cultural Mosaic", "四方连续", "民族风", "橙", "纹样",
                                        "https://images.unsplash.com/photo-1576485290814-1c72aa4bbb8e?auto=format&fit=crop&w=800&q=80");
                        createPattern("Floral Grid", "四方连续", "复古风", "粉", "植物",
                                        "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?auto=format&fit=crop&w=800&q=80");
                        createPattern("Modern Tiles", "四方连续", "极简风", "白", "几何图形",
                                        "https://images.unsplash.com/photo-1604871000636-074fa5117945?auto=format&fit=crop&w=800&q=80");
                        createPattern("Tech Grid", "四方连续", "未来主义", "蓝", "几何图形",
                                        "https://images.unsplash.com/photo-1635070041078-e363dbe005cb?auto=format&fit=crop&w=800&q=80");

                        // 旋转对称
                        createPattern("Neon Lines", "旋转对称", "未来主义", "紫", "几何图形",
                                        "https://images.unsplash.com/photo-1550684847-75bdda21cc95?auto=format&fit=crop&w=800&q=80");
                        createPattern("Mandala Flower", "旋转对称", "民族风", "黄", "植物",
                                        "https://images.unsplash.com/photo-1544551763-46a013bb70d5?auto=format&fit=crop&w=800&q=80");
                        createPattern("Zen Circle", "旋转对称", "新中式", "黑", "纹样",
                                        "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?auto=format&fit=crop&w=800&q=80");

                        // 单体构成
                        createPattern("Vintage Floral", "单体构成", "复古风", "粉", "植物",
                                        "https://images.unsplash.com/photo-1501004318641-b39e6451bec6?auto=format&fit=crop&w=800&q=80");
                        createPattern("Dragon Emblem", "单体构成", "传统风", "黄", "动物",
                                        "https://images.unsplash.com/photo-1578662996442-48f60103fc96?auto=format&fit=crop&w=800&q=80");
                        createPattern("Minimal Icon", "单体构成", "极简风", "黑", "几何图形",
                                        "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=800&q=80");
                        createPattern("Cyber Symbol", "单体构成", "科技风", "蓝", "几何图形",
                                        "https://images.unsplash.com/photo-1634017839464-5c339ebe3cb4?auto=format&fit=crop&w=800&q=80");

                        // 镜像对称 (Ex Scatter)
                        createPattern("Leafy Green", "镜像对称", "新中式", "绿", "植物",
                                        "https://images.unsplash.com/photo-1477414348463-c0eb7f1359b6?auto=format&fit=crop&w=800&q=80");
                        createPattern("Starry Night", "镜像对称", "现代主义", "蓝", "几何图形",
                                        "https://images.unsplash.com/photo-1419242902214-272b3f66ee7a?auto=format&fit=crop&w=800&q=80");
                        createPattern("Vintage Dots", "镜像对称", "复古风", "红", "植物",
                                        "https://images.unsplash.com/photo-1518895949257-7621c3c786d7?auto=format&fit=crop&w=800&q=80");

                        // New Tags Showcase
                        createPattern("Pop Art Faces", "四方连续", "波普风", "红", "人物",
                                        "https://images.unsplash.com/photo-1549490349-8643362247b5?auto=format&fit=crop&w=800&q=80");
                        createPattern("Abstract Shapes", "单体构成", "抽象风", "橙", "抽象图形",
                                        "https://images.unsplash.com/photo-1541701494587-cb58502866ab?auto=format&fit=crop&w=800&q=80");
                        createPattern("Gothic Arch", "镜像对称", "西方古典", "灰", "建筑与空间",
                                        "https://images.unsplash.com/photo-1518640467707-6811f4a6ab73?auto=format&fit=crop&w=800&q=80");
                        createPattern("Festive Lanterns", "散点式", "节庆风", "红", "器物",
                                        "https://images.unsplash.com/photo-1509937528035-ad76254b0356?auto=format&fit=crop&w=800&q=80");

                        System.out.println("Initialized mock patterns with new classification tags.");
                }
        }

        private void createPattern(String name, String form, String style, String color, String theme, String url) {
                Pattern p = new Pattern();
                p.setName(name);
                p.setForm(form);
                p.setStyle(style);
                p.setColor(color);
                p.setTheme(theme);
                p.setImageUrl(url);
                p.setCreateTime(LocalDateTime.now());
                p.setUpdateTime(LocalDateTime.now());
                patternRepository.save(p);
        }
}
