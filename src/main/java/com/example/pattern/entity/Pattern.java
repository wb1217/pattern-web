package com.example.pattern.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pattern_info")
public class Pattern {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name; // 纹样名称
    private String category; // 纹样分类
    private String description; // 纹样描述
    private String imageUrl; // 补充：纹样图片路径

    // 4 Attributes for Faceted Search
    private String form; // 形式结构
    private String style; // 风格特征
    private String color; // 色彩
    private String theme; // 主题元素

    private Integer sort; // 补充：排序权重
    private LocalDateTime createTime; // 补充：创建时间
    private LocalDateTime updateTime; // 补充：更新时间
}