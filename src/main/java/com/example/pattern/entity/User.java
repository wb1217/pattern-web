package com.example.pattern.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sys_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private Integer status; // 1=管理员, 2=普通用户, 0=禁用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 状态常量
    public static final int STATUS_DISABLED = 0; // 禁用
    public static final int STATUS_ADMIN = 1; // 管理员
    public static final int STATUS_USER = 2; // 普通用户

    // 判断是否为管理员
    public boolean isAdmin() {
        return this.status != null && this.status == STATUS_ADMIN;
    }
}