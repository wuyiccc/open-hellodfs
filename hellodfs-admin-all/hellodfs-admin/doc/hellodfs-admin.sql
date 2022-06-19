-- 建立数据库
CREATE DATABASE `hellodfs-admin` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';

CREATE TABLE IF NOT EXISTS `user` (
    `id` varchar(255) NOT NULL PRIMARY KEY,
    `username` varchar(255) COMMENT '用户名',
    `nickname` varchar(255) COMMENT '昵称',
    `email` varchar(255) COMMENT '邮箱',
    `password` varchar(255) COMMENT '密码',
    `avatar` varchar(255) COMMENT '头像',
    `phone` varchar(11) COMMENT '手机',
    `sex` varchar(1) COMMENT '性别',
    `description` text COMMENT '个人描述',
    `total_size` int(11) COMMENT '网盘总大小，单位:kb',
    `used_size` int(11) COMMENT '网盘已使用，单位:kb',
    `created_time` datetime COMMENT '创建时间',
    `updated_time` datetime COMMENT '更新时间'
    );