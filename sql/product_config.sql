-- ----------------------------
-- 产品基础信息临时表结构
-- ----------------------------
DROP TABLE IF EXISTS `txwx_product_basic_info_temp`;
CREATE TABLE `txwx_product_basic_info_temp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `product_type` char(1) NOT NULL COMMENT '产品类型（1-大模型 2-小装置）',
  `product_name` varchar(200) NOT NULL COMMENT '产品名称',
  `product_introduction` varchar(1000) DEFAULT NULL COMMENT '产品简介',
  `background_image_url` varchar(500) DEFAULT NULL COMMENT '背景图片地址',
  `detailed_introduction` text COMMENT '详细介绍',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT  '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_type` (`product_type`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='产品基础信息临时表';

-- ----------------------------
-- 产品基础信息正式表结构
-- ----------------------------
DROP TABLE IF EXISTS `txwx_product_basic_info`;
CREATE TABLE `txwx_product_basic_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `product_type` char(1) NOT NULL COMMENT '产品类型（1-大模型 2-小装置）',
  `product_name` varchar(200) NOT NULL COMMENT '产品名称',
  `product_introduction` varchar(1000) DEFAULT NULL COMMENT '产品简介',
  `background_image_url` varchar(500) DEFAULT NULL COMMENT '背景图片地址',
  `detailed_introduction` text COMMENT '详细介绍',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT  '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_type` (`product_type`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='产品基础信息正式表';

-- ----------------------------
-- 应用场景临时表结构
-- ----------------------------
DROP TABLE IF EXISTS `txwx_application_scenario_temp`;
CREATE TABLE `txwx_application_scenario_temp` (
  `scenario_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '应用场景主键',
  `product_type` char(1) NOT NULL COMMENT '产品类型（1-大模型 2-小装置）',
  `scenario_name` varchar(200) NOT NULL COMMENT '应用场景名称',
  `scenario_introduction` varchar(1000) DEFAULT NULL COMMENT '应用场景简介',
  `icon_url` varchar(500) DEFAULT NULL COMMENT '图标地址',
  `background_image_url` varchar(500) DEFAULT NULL COMMENT '背景图片地址',
  `detailed_introduction` text COMMENT '详情介绍',
  `sort_order` int(4) DEFAULT '0' COMMENT '排序序号',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT  '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`scenario_id`),
  KEY `idx_product_type` (`product_type`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='应用场景临时表';

-- ----------------------------
-- 应用场景正式表结构
-- ----------------------------
DROP TABLE IF EXISTS `txwx_application_scenario`;
CREATE TABLE `txwx_application_scenario` (
  `scenario_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '应用场景主键',
  `product_type` char(1) NOT NULL COMMENT '产品类型（1-大模型 2-小装置）',
  `scenario_name` varchar(200) NOT NULL COMMENT '应用场景名称',
  `scenario_introduction` varchar(1000) DEFAULT NULL COMMENT '应用场景简介',
  `icon_url` varchar(500) DEFAULT NULL COMMENT '图标地址',
  `background_image_url` varchar(500) DEFAULT NULL COMMENT '背景图片地址',
  `detailed_introduction` text COMMENT '详情介绍',
  `sort_order` int(4) DEFAULT '0' COMMENT '排序序号',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT  '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`scenario_id`),
  KEY `idx_product_type` (`product_type`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='应用场景正式表';

-- ----------------------------
-- 典型案例/典型产品临时表结构
-- ----------------------------
DROP TABLE IF EXISTS `txwx_typical_case_product_temp`;
CREATE TABLE `txwx_typical_case_product_temp` (
  `case_product_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '案例/产品主键',
  `product_type` char(1) NOT NULL COMMENT '产品类型（1-大模型 2-小装置）',
  `product_name` varchar(200) NOT NULL COMMENT '案例/产品名称',
  `product_introduction` varchar(1000) DEFAULT NULL COMMENT '案例/产品简介',
  `standard_functions_str` text COMMENT '标配功能（逗号分隔的字符串）',
  `custom_services_str` text COMMENT '定制服务（逗号分隔的字符串）',
  `image_url` varchar(500) DEFAULT NULL COMMENT '图片地址',
  `detailed_introduction` text COMMENT '详情介绍',
  `sort_order` int(4) DEFAULT '0' COMMENT '排序序号',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT  '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`case_product_id`),
  KEY `idx_product_type` (`product_type`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='典型案例/典型产品临时表';

-- ----------------------------
-- 典型案例/典型产品正式表结构
-- ----------------------------
DROP TABLE IF EXISTS `txwx_typical_case_product`;
CREATE TABLE `txwx_typical_case_product` (
  `case_product_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '案例/产品主键',
  `product_type` char(1) NOT NULL COMMENT '产品类型（1-大模型 2-小装置）',
  `product_name` varchar(200) NOT NULL COMMENT '案例/产品名称',
  `product_introduction` varchar(1000) DEFAULT NULL COMMENT '案例/产品简介',
  `standard_functions_str` text COMMENT '标配功能（逗号分隔的字符串）',
  `custom_services_str` text COMMENT '定制服务（逗号分隔的字符串）',
  `image_url` varchar(500) DEFAULT NULL COMMENT '图片地址',
  `detailed_introduction` text COMMENT '详情介绍',
  `sort_order` int(4) DEFAULT '0' COMMENT '排序序号',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT  '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`case_product_id`),
  KEY `idx_product_type` (`product_type`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='典型案例/典型产品正式表';

-- ----------------------------
-- 数据复制操作（临时表到正式表）
-- ----------------------------

-- 复制产品基础信息
-- INSERT INTO txwx_product_basic_info (product_type, product_name, product_intro, background_image_url, detail_intro, create_by, create_time, update_by, update_time, remark)
-- SELECT product_type, product_name, product_intro, background_image_url, detail_intro, create_by, create_time, update_by, update_time, remark 
-- FROM txwx_product_basic_info_temp;

-- 复制应用场景
-- INSERT INTO application_scenario (scenario_id, product_type, scenario_name, scenario_introduction, icon_url, background_image_url, detailed_introduction, sort_order, create_by, create_time, update_by, update_time, remark)
-- SELECT scenario_id, product_type, scenario_name, scenario_introduction, icon_url, background_image_url, detailed_introduction, sort_order, create_by, create_time, update_by, update_time, remark 
-- FROM application_scenario_temp;

-- 复制典型案例/产品
-- INSERT INTO typical_case_product (case_product_id, product_type, case_product_name, case_product_introduction, standard_functions_str, custom_services_str, image_url, detailed_introduction, sort_order, create_by, create_time, update_by, update_time, remark)
-- SELECT case_product_id, product_type, case_product_name, case_product_introduction, standard_functions_str, custom_services_str, image_url, detailed_introduction, sort_order, create_by, create_time, update_by, update_time, remark 
-- FROM typical_case_product_temp;

-- ----------------------------
-- 清空表操作
-- ----------------------------

-- 清空临时表
-- TRUNCATE TABLE product_basic_info_temp;
-- TRUNCATE TABLE application_scenario_temp;
-- TRUNCATE TABLE typical_case_product_temp;

-- 清空正式表
-- TRUNCATE TABLE product_basic_info;
-- TRUNCATE TABLE application_scenario;
-- TRUNCATE TABLE typical_case_product;

-- ----------------------------
-- 根据产品类型复制数据
-- ----------------------------

-- 根据产品类型复制产品基础信息
-- INSERT INTO product_basic_info (product_type, product_name, product_introduction, background_image_url, detailed_introduction, create_by, create_time, update_by, update_time, remark)
-- SELECT product_type, product_name, product_introduction, background_image_url, detailed_introduction, create_by, create_time, update_by, update_time, remark 
-- FROM product_basic_info_temp WHERE product_type = '1';

-- 根据产品类型复制应用场景
-- INSERT INTO application_scenario (scenario_id, product_type, scenario_name, scenario_introduction, icon_url, background_image_url, detailed_introduction, sort_order, create_by, create_time, update_by, update_time, remark)
-- SELECT scenario_id, product_type, scenario_name, scenario_introduction, icon_url, background_image_url, detailed_introduction, sort_order, create_by, create_time, update_by, update_time, remark 
-- FROM application_scenario_temp WHERE product_type = '1';

-- 根据产品类型复制典型案例/产品
-- INSERT INTO typical_case_product (case_product_id, product_type, case_product_name, case_product_introduction, standard_functions_str, custom_services_str, image_url, detailed_introduction, sort_order, create_by, create_time, update_by, update_time, remark)
-- SELECT case_product_id, product_type, case_product_name, case_product_introduction, standard_functions_str, custom_services_str, image_url, detailed_introduction, sort_order, create_by, create_time, update_by, update_time, remark 
-- FROM typical_case_product_temp WHERE product_type = '1';

-- ----------------------------
-- 示例数据
-- ----------------------------

-- 产品基础信息示例
-- INSERT INTO product_basic_info_temp (product_type, product_name, product_introduction, background_image_url, detailed_introduction, create_by, create_time) VALUES
-- ('1', 'AI大模型平台', '基于深度学习的智能大模型平台', 'https://example.com/ai-model-bg.jpg', '这是一个功能强大的AI大模型平台，支持多种应用场景...', 'admin', NOW()),
-- ('2', '智能小装置', '便携式智能硬件设备', 'https://example.com/device-bg.jpg', '这是一款轻便易用的智能小装置，适用于各种移动场景...', 'admin', NOW());

-- 应用场景示例
-- INSERT INTO application_scenario_temp (product_type, scenario_name, scenario_introduction, icon_url, background_image_url, detailed_introduction, sort_order, create_by, create_time) VALUES
-- ('1', '智能客服', '提供7x24小时智能客服服务', 'https://example.com/icon1.png', 'https://example.com/bg1.jpg', '智能客服场景详细介绍...', 1, 'admin', NOW()),
-- ('1', '数据分析', '强大的数据分析能力', 'https://example.com/icon2.png', 'https://example.com/bg2.jpg', '数据分析场景详细介绍...', 2, 'admin', NOW()),
-- ('2', '移动办公', '随时随地处理工作', 'https://example.com/icon3.png', 'https://example.com/bg3.jpg', '移动办公场景详细介绍...', 1, 'admin', NOW());

-- 典型案例/产品示例
-- INSERT INTO typical_case_product_temp (product_type, case_product_name, case_product_introduction, standard_functions_str, custom_services_str, image_url, detailed_introduction, sort_order, create_by, create_time) VALUES
-- ('1', '智能客服解决方案', '为企业提供完整的智能客服解决方案', '自然语言理解,多轮对话,情感分析', '定制化开发,私有化部署,专业培训', 'https://example.com/case1.jpg', '智能客服解决方案详细介绍...', 1, 'admin', NOW()),
-- ('1', '数据分析平台', '企业级大数据分析平台', '数据采集,实时分析,可视化报表', '定制化算法,私有化部署,专业技术支持', 'https://example.com/case2.jpg', '数据分析平台详细介绍...', 2, 'admin', NOW()),
-- ('2', '便携式翻译器', '支持多语言实时翻译的便携设备', '语音识别,实时翻译,离线使用', '定制化外观,专属应用,批量采购', 'https://example.com/device1.jpg', '便携式翻译器详细介绍...', 1, 'admin', NOW());

-- ----------------------------
-- 索引优化
-- ----------------------------

-- 为应用场景创建复合索引
-- CREATE INDEX idx_application_scenario_temp_type_sort ON application_scenario_temp(product_type, sort_order);
-- CREATE INDEX idx_application_scenario_type_sort ON application_scenario(product_type, sort_order);

-- 为案例产品创建复合索引
-- CREATE INDEX idx_typical_case_product_temp_type_sort ON typical_case_product_temp(product_type, sort_order);
-- CREATE INDEX idx_typical_case_product_type_sort ON typical_case_product(product_type, sort_order);

-- ----------------------------
-- 注释说明
-- ----------------------------
-- 产品类型说明：
-- 1-大模型：指AI大模型相关产品配置
-- 2-小装置：指智能硬件小装置相关产品配置

-- 表结构说明：
-- 临时表（*_temp）：用于保存操作和预览操作，数据可修改
-- 正式表：用于发布后的展示操作，数据相对稳定

-- 特殊字段说明：
-- standard_functions_str 和 custom_services_str：存储为逗号分隔的字符串，在Java实体中会转换为List结构
-- detailed_introduction：长文本字段，支持存储详细的介绍信息

-- 工作流程：
-- 1. 保存：数据存入临时表
-- 2. 预览：从临时表读取数据
-- 3. 发布：将临时表数据复制到正式表
-- 4. 展示：从正式表读取数据用于前端显示