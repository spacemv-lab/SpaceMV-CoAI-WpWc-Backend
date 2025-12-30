-- ----------------------------
-- Table structure for company_info_temp (临时表)
-- ----------------------------
DROP TABLE IF EXISTS `txwx_company_info_temp`;
CREATE TABLE `txwx_company_info_temp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `company_name` varchar(200) NOT NULL COMMENT '公司名称',
  `copyright_info` varchar(500) DEFAULT NULL COMMENT '版权信息',
  `version_number` varchar(50) DEFAULT NULL COMMENT '版本号',
  `company_address` varchar(500) DEFAULT NULL COMMENT '公司地址',
  `security_record` varchar(100) DEFAULT NULL COMMENT '网安备案信息',
  `icp_record` varchar(100) DEFAULT NULL COMMENT 'ICP备案信息',
  `business_cooperation` varchar(500) DEFAULT NULL COMMENT '商务合作途径',
  `resume_delivery` varchar(500) DEFAULT NULL COMMENT '简历投递途径',
  `logo_url` varchar(500) DEFAULT NULL COMMENT '公司Logo URL',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT  '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='公司基本信息临时表';

-- ----------------------------
-- Table structure for company_info (正式表)
-- ----------------------------
DROP TABLE IF EXISTS `txwx_company_info`;
CREATE TABLE `txwx_company_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `company_name` varchar(200) NOT NULL COMMENT '公司名称',
  `copyright_info` varchar(500) DEFAULT NULL COMMENT '版权信息',
  `version_number` varchar(50) DEFAULT NULL COMMENT '版本号',
  `company_address` varchar(500) DEFAULT NULL COMMENT '公司地址',
  `security_record` varchar(100) DEFAULT NULL COMMENT '网安备案信息',
  `icp_record` varchar(100) DEFAULT NULL COMMENT 'ICP备案信息',
  `business_cooperation` varchar(500) DEFAULT NULL COMMENT '商务合作途径',
  `resume_delivery` varchar(500) DEFAULT NULL COMMENT '简历投递途径',
  `logo_url` varchar(500) DEFAULT NULL COMMENT '公司Logo URL',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT  '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='公司基本信息正式表';

-- ----------------------------
-- Table structure for focus_temp (临时表)
-- ----------------------------
DROP TABLE IF EXISTS `txwx_focus_temp`;
CREATE TABLE `txwx_focus_temp` (
  `focus_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `focus_name` varchar(100) NOT NULL COMMENT '名称',
  `image_url` varchar(500) NOT NULL COMMENT '图片地址',
  `sort_order` int(4) DEFAULT '0' COMMENT '排序序号',
  `jump_url` varchar(500) DEFAULT NULL COMMENT '跳转链接',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT  '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`focus_id`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='关注配置临时表';

-- ----------------------------
-- Table structure for focus (正式表)
-- ----------------------------
DROP TABLE IF EXISTS `txwx_focus`;
CREATE TABLE `txwx_focus` (
  `focus_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `focus_name` varchar(100) NOT NULL COMMENT '名称',
  `image_url` varchar(500) NOT NULL COMMENT '图片地址',
  `sort_order` int(4) DEFAULT '0' COMMENT '排序序号',
  `jump_url` varchar(500) DEFAULT NULL COMMENT '跳转链接',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT  '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`focus_id`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='关注配置正式表';

-- ----------------------------
-- Table structure for product_cert_temp (临时表)
-- ----------------------------
DROP TABLE IF EXISTS `txwx_product_cert_temp`;
CREATE TABLE `txwx_product_cert_temp` (
  `cert_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `cert_name` varchar(200) NOT NULL COMMENT '产品认证名称',
  `image_url` varchar(500) NOT NULL COMMENT '图片地址',
  `sort_order` int(4) DEFAULT '0' COMMENT '排序序号',
  `jump_url` varchar(500) DEFAULT NULL COMMENT '跳转链接',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT  '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`cert_id`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='产品认证临时表';

-- ----------------------------
-- Table structure for product_cert (正式表)
-- ----------------------------
DROP TABLE IF EXISTS `txwx_product_cert`;
CREATE TABLE `txwx_product_cert` (
  `cert_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `cert_name` varchar(200) NOT NULL COMMENT '产品认证名称',
  `image_url` varchar(500) NOT NULL COMMENT '图片地址',
  `sort_order` int(4) DEFAULT '0' COMMENT '排序序号',
  `jump_url` varchar(500) DEFAULT NULL COMMENT '跳转链接',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT  '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`cert_id`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='产品认证正式表';

-- ----------------------------
-- Table structure for company_profile_temp (临时表)
-- ----------------------------
DROP TABLE IF EXISTS `txwx_company_profile_temp`;
CREATE TABLE `txwx_company_profile_temp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `profile_one` text COMMENT '公司简介一（富文本）',
  `profile_two` text COMMENT '公司简介二（富文本）',
  `profile_three` text COMMENT '公司简介三（富文本）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT  '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='公司简介临时表';

-- ----------------------------
-- Table structure for company_profile (正式表)
-- ----------------------------
DROP TABLE IF EXISTS `txwx_company_profile`;
CREATE TABLE `txwx_company_profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `profile_one` text COMMENT '公司简介一（富文本）',
  `profile_two` text COMMENT '公司简介二（富文本）',
  `profile_three` text COMMENT '公司简介三（富文本）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT  '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='公司简介正式表';

-- ----------------------------
-- Records (初始数据为空)
-- ----------------------------
-- ----------------------------
-- 临时表到正式表的数据复制操作
-- ----------------------------
-- 复制txwx_page_button_temp到txwx_page_button
-- TRUNCATE TABLE txwx_page_button;
-- INSERT INTO txwx_page_button (button_id, page_code, button_type, button_text, background_color, jump_url, image_url, is_show, is_publish, state, sort_order, create_by, create_time, update_by, update_time, saved_by, saved_time, published_by, published_time, remark)
-- SELECT button_id, page_code, button_type, button_text, background_color, jump_url, image_url, is_show, is_publish, state, sort_order, create_by, create_time, update_by, update_time, saved_by, saved_time, published_by, published_time, remark FROM txwx_page_button_temp;