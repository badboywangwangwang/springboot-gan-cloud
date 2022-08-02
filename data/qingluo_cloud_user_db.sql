/*
 Navicat Premium Data Transfer

 Source Server         : nacos
 Source Server Type    : MySQL
 Source Server Version : 50731
 Source Host           : localhost:3306
 Source Schema         : qingluo_cloud_user_db

 Target Server Type    : MySQL
 Target Server Version : 50731
 File Encoding         : 65001

 Date: 15/07/2022 18:07:12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for qingluo_admin_user
-- ----------------------------
DROP TABLE IF EXISTS `qingluo_admin_user`;
CREATE TABLE `qingluo_admin_user` (
  `admin_user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '管理员id',
  `login_user_name` varchar(50) NOT NULL COMMENT '管理员登陆名称',
  `login_password` varchar(50) NOT NULL COMMENT '管理员登陆密码',
  `nick_name` varchar(50) NOT NULL COMMENT '管理员显示昵称',
  `locked` tinyint(4) DEFAULT '0' COMMENT '是否锁定 0未锁定 1已锁定无法登陆',
  PRIMARY KEY (`admin_user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of qingluo_admin_user
-- ----------------------------
BEGIN;
INSERT INTO `qingluo_admin_user` VALUES (1, 'qingluo', 'e10adc3949ba59abbe56e057f20f883e', 'qingluo', 0);
COMMIT;

-- ----------------------------
-- Table structure for qingluo_user
-- ----------------------------
DROP TABLE IF EXISTS `qingluo_user`;
CREATE TABLE `qingluo_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户主键id',
  `nick_name` varchar(50) DEFAULT '' COMMENT '用户昵称',
  `login_name` varchar(11) NOT NULL DEFAULT '' COMMENT '登陆名称(默认为手机号)',
  `password_md5` varchar(32) NOT NULL DEFAULT '' COMMENT 'MD5加密后的密码',
  `introduce_sign` varchar(100) DEFAULT '' COMMENT '个性签名',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '注销标识字段(0-正常 1-已注销)',
  `locked_flag` tinyint(4) DEFAULT '0' COMMENT '锁定标识字段(0-未锁定 1-已锁定)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `version` int(1) DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of qingluo_user
-- ----------------------------
BEGIN;
INSERT INTO `qingluo_user` VALUES (1, 'qingluo', '13649471098', 'e10adc3949ba59abbe56e057f20f883e', '1', 0, 0, '2020-08-19 00:00:00', '2020-08-19 00:00:00', 0);
INSERT INTO `qingluo_user` VALUES (2, '十三', '13700002703', 'e10adc3949ba59abbe56e057f20f883e', '我不怕千万人阻挡，只怕自己投降', 0, 0, '2022-05-22 08:44:57', '2022-05-22 08:44:57', 0);
INSERT INTO `qingluo_user` VALUES (3, '', '13649471599', 'e10adc3949ba59abbe56e057f20f883e', '', 0, 0, '2022-07-15 18:06:03', NULL, 0);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
