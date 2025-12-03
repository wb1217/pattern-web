-- Create Database
CREATE DATABASE IF NOT EXISTS pattern_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pattern_db;
-- Table: sys_user
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `username` varchar(50) NOT NULL COMMENT 'Username',
    `password` varchar(100) NOT NULL COMMENT 'Password',
    `nickname` varchar(50) DEFAULT NULL COMMENT 'Nickname',
    `status` int(11) DEFAULT '1' COMMENT 'Status (1: Normal, 0: Disabled)',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = 'User Table';
-- Table: pattern_info
DROP TABLE IF EXISTS `pattern_info`;
CREATE TABLE `pattern_info` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `name` varchar(100) NOT NULL COMMENT 'Pattern Name',
    `category` varchar(50) DEFAULT NULL COMMENT 'Category',
    `description` text COMMENT 'Description',
    `image_url` varchar(255) DEFAULT NULL COMMENT 'Image URL',
    `form` varchar(50) DEFAULT NULL COMMENT 'Attr: Form Structure',
    `style` varchar(50) DEFAULT NULL COMMENT 'Attr: Style Feature',
    `color` varchar(50) DEFAULT NULL COMMENT 'Attr: Color',
    `theme` varchar(50) DEFAULT NULL COMMENT 'Attr: Theme Element',
    `sort` int(11) DEFAULT '0' COMMENT 'Sort Order',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = 'Texture Pattern Table';