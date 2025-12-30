-- ----------------------------
-- 页面按钮配置临时表结构
-- ----------------------------
DROP TABLE IF EXISTS `txwx_page_button_temp`;
CREATE TABLE `txwx_page_button_temp` (
  `button_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '按钮主键',
  `page_code` varchar(100) NOT NULL COMMENT '页面标识',
  `button_type` char(1) NOT NULL COMMENT '按钮类型（1-文字按钮 2-图片按钮）',
  `button_text` varchar(200) DEFAULT NULL COMMENT '按钮文案',
  `background_color` varchar(20) DEFAULT NULL COMMENT '背景色',
  `jump_url` varchar(500) DEFAULT NULL COMMENT '跳转地址',
  `image_url` varchar(500) DEFAULT NULL COMMENT '图片URL',
  `is_show` char(1) DEFAULT '1' COMMENT '是否对外展示（0-否 1-是）',
  `is_publish` char(1) DEFAULT '1' COMMENT '是否已发布（0-否 1-是）',
  `state` char(1) DEFAULT '1' COMMENT '形态 0-nav路由 1-新窗口打开链接 2-当前窗口打开链接 3-正在开发中',
  `sort_order` int(4) DEFAULT '0' COMMENT '排序序号',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`button_id`),
  KEY `idx_page_code` (`page_code`),
  KEY `idx_button_type` (`button_type`),
  KEY `idx_is_show` (`is_show`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='页面按钮配置临时表';

-- ----------------------------
-- Table structure for page_button
-- ----------------------------
DROP TABLE IF EXISTS `txwx_page_button`;
CREATE TABLE `txwx_page_button` (
  `button_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '按钮主键',
  `page_code` varchar(100) NOT NULL COMMENT '页面标识',
  `button_type` char(1) NOT NULL COMMENT '按钮类型（1-文字按钮 2-图片按钮）',
  `button_text` varchar(200) DEFAULT NULL COMMENT '按钮文案',
  `background_color` varchar(20) DEFAULT NULL COMMENT '背景色',
  `jump_url` varchar(500) DEFAULT NULL COMMENT '跳转地址',
  `image_url` varchar(500) DEFAULT NULL COMMENT '图片URL',
  `is_show` char(1) DEFAULT '1' COMMENT '是否对外展示（0-否 1-是）',
  `is_publish` char(1) DEFAULT '1' COMMENT '是否已发布（0-否 1-是）',
  `state` char(1) DEFAULT '1' COMMENT '形态 0-nav路由 1-新窗口打开链接 2-当前窗口打开链接 3-正在开发中',
  `sort_order` int(4) DEFAULT '0' COMMENT '排序序号',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`button_id`),
  KEY `idx_page_code` (`page_code`),
  KEY `idx_button_type` (`button_type`),
  KEY `idx_is_show` (`is_show`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='页面按钮配置表';

-- ----------------------------
-- Records of page_button
-- ----------------------------

-- ----------------------------
-- 为正式表page_button添加清空操作的存储过程（如果需要的话）
-- ----------------------------

-- ----------------------------
-- 临时表到正式表的数据复制操作
-- ----------------------------

-- 复制page_button_temp到page_button
-- TRUNCATE TABLE page_button;
-- INSERT INTO page_button (button_id, page_code, button_type, button_text, background_color, jump_url, image_url, is_show, sort_order, create_by, create_time, update_by, update_time, remark)
-- SELECT button_id, page_code, button_type, button_text, background_color, jump_url, image_url, is_show, sort_order, create_by, create_time, update_by, update_time, remark FROM page_button_temp;

-- ----------------------------
-- 临时表清空操作
-- ----------------------------
-- TRUNCATE TABLE page_button_temp;

-- ----------------------------
-- 正式表清空操作  
-- ----------------------------
-- TRUNCATE TABLE page_button;

-- ----------------------------
-- 示例数据
-- ----------------------------
-- INSERT INTO page_button_temp (page_code, button_type, button_text, background_color, jump_url, is_show, sort_order, create_by, create_time) VALUES
-- ('home', '1', '首页', '#007bff', '/home', '1', 1, 'admin', NOW()),
-- ('about', '2', NULL, '#28a745', '/about', '1', 2, 'admin', NOW());

-- ----------------------------
-- 索引优化（根据查询需求）
-- ----------------------------
-- CREATE INDEX idx_page_button_temp_page_code_is_show ON page_button_temp(page_code, is_show);
-- CREATE INDEX idx_page_button_temp_sort_order ON page_button_temp(sort_order);

-- ----------------------------
-- 注释说明
-- ----------------------------
-- 临时表(page_button_temp)用于保存操作，用户在管理界面配置的按钮信息先保存到这里
-- 正式表(page_button)用于展示操作，发布时将临时表数据复制到正式表，生产环境从正式表读取数据
-- 预览操作从临时表读取数据，可以实时查看配置效果
-- 发布操作会将临时表数据完整复制到正式表，覆盖正式表的所有数据