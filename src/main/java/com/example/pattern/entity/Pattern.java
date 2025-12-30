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
    // Category removed

    private String description; // 纹样描述
    private String imageUrl; // 补充：纹样图片路径

    // 4 Attributes for Faceted Search
    private String form; // 形式结构
    private String style; // 风格特征
    private String color; // 色彩
    private String theme; // 主题元素

    // Basic Information
    private String imageCode; // 图片编号
    private String resolution; // 图片尺寸/分辨率
    private String imageFormat; // 图片格式
    private String copyright; // 版权状态
    private String author; // 作者
    private String originDate; // 创作时间
    private String recorder; // 录入者

    private String series; // 系列名称

    // Sort removed
    private LocalDateTime createTime; // 补充：创建时间
    private LocalDateTime updateTime; // 补充：更新时间
}