-- ----------------------------
-- 首页轮播图配置临时表结构
-- ----------------------------
DROP TABLE IF EXISTS `txwx_carousel_image_temp`;
CREATE TABLE `txwx_carousel_image_temp` (
  `carousel_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '轮播图主键',
  `image_url` varchar(500) NOT NULL COMMENT '图片地址',
  `is_show` char(1) NOT NULL COMMENT '是否显示（0-否 1-是）',
  `sort_order` int(4) DEFAULT '0' COMMENT '排序序号',
  `title` varchar(200) DEFAULT NULL COMMENT '标题',
  `description` varchar(400) DEFAULT '' COMMENT '描述',
  `jump_url` varchar(200) DEFAULT '' COMMENT '跳转链接',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT  '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`carousel_id`),
  KEY `idx_is_show` (`is_show`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='首页轮播图配置临时表';

DROP TABLE IF EXISTS `txwx_carousel_image`;
CREATE TABLE `txwx_carousel_image` (
   `carousel_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '轮播图主键',
   `image_url` varchar(500) NOT NULL COMMENT '图片地址',
   `is_show` char(1) DEFAULT '1' COMMENT '是否显示（0-否 1-是）',
   `sort_order` int(4) DEFAULT '0' COMMENT '排序序号',
   `title` varchar(200) DEFAULT NULL COMMENT '标题',
   `description` varchar(400) DEFAULT '' COMMENT '描述',
   `jump_url` varchar(200) DEFAULT '' COMMENT '跳转链接',
   `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
   `create_time` datetime DEFAULT NULL COMMENT '创建时间',
   `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
   `update_time` datetime DEFAULT NULL COMMENT '更新时间',
   `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
   `saved_time` datetime DEFAULT NULL COMMENT  '末次保存时间',
   `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
   `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
   `remark` varchar(500) DEFAULT NULL COMMENT '备注',
   PRIMARY KEY (`carousel_id`),
   KEY `idx_is_show` (`is_show`),
   KEY `idx_sort_order` (`sort_order`),
   KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='首页轮播图配置表';

-- ----------------------------
-- 首页主打产品配置临时表结构
-- ----------------------------
DROP TABLE IF EXISTS `txwx_main_product_temp`;
CREATE TABLE `txwx_main_product_temp` (
  `product_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主打产品主键',
  `product_name` varchar(200) NOT NULL COMMENT '产品名称',
  `image_url` varchar(500) NOT NULL COMMENT '产品图片地址',
  `description` varchar(1000) DEFAULT NULL COMMENT '产品描述',
  `jump_url` varchar(500) DEFAULT NULL COMMENT '跳转链接',
  `is_show` char(1) NOT NULL COMMENT '是否显示（0-否 1-是）',
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
  PRIMARY KEY (`product_id`),
  KEY `idx_is_show` (`is_show`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='首页主打产品配置临时表';

DROP TABLE IF EXISTS `txwx_main_product`;
CREATE TABLE `txwx_main_product` (
  `product_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '产品主键',
  `product_name` varchar(200) NOT NULL COMMENT '产品名称',
  `image_url` varchar(500) NOT NULL COMMENT '产品图片地址',
  `description` varchar(1000) DEFAULT NULL COMMENT '产品描述',
  `is_show` char(1) DEFAULT '1' COMMENT '是否显示（0-否 1-是）',
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
  PRIMARY KEY (`product_id`),
  KEY `idx_product_name` (`product_name`),
  KEY `idx_is_show` (`is_show`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='首页主要产品配置表';

-- ----------------------------
-- 首页典型客户配置临时表结构
-- ----------------------------
DROP TABLE IF EXISTS `txwx_typical_customer_temp`;
CREATE TABLE `txwx_typical_customer_temp` (
  `customer_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '典型客户主键',
  `customer_name` varchar(200) NOT NULL COMMENT '客户名称',
  `image_url` varchar(500) NOT NULL COMMENT '客户Logo地址',
  `jump_url` varchar(500) DEFAULT NULL COMMENT '跳转链接',
  `is_show` char(1) NOT NULL COMMENT '是否显示（0-否 1-是）',
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
  PRIMARY KEY (`customer_id`),
  KEY `idx_is_show` (`is_show`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='首页典型客户配置临时表';

DROP TABLE IF EXISTS `txwx_typical_customer`;
CREATE TABLE `txwx_typical_customer` (
  `customer_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '客户主键',
  `customer_name` varchar(200) DEFAULT NULL COMMENT '客户名称',
  `image_url` varchar(500) NOT NULL COMMENT '客户Logo地址',
  `is_show` char(1) DEFAULT '1' COMMENT '是否显示（0-否 1-是）',
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
  PRIMARY KEY (`customer_id`),
  KEY `idx_customer_name` (`customer_name`),
  KEY `idx_is_show` (`is_show`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='首页典型客户配置表';

-- ----------------------------
-- 为正式表添加清空操作的存储过程（如果需要的话）
-- ----------------------------

-- ----------------------------
-- 临时表到正式表的数据复制操作
-- ----------------------------

-- 复制carousel_image_temp到carousel_image
-- TRUNCATE TABLE carousel_image;
-- INSERT INTO carousel_image (carousel_id, image_url, is_show, sort_order, title, description, jump_url, create_by, create_time, update_by, update_time, remark)
-- SELECT carousel_id, image_url, is_show, sort_order, title, description, jump_url, create_by, create_time, update_by, update_time, remark FROM carousel_image_temp;

-- 复制main_product_temp到main_product
-- TRUNCATE TABLE main_product;
-- INSERT INTO main_product (product_id, product_name, product_image, product_description, product_link, is_show, sort_order, create_by, create_time, update_by, update_time, remark)
-- SELECT product_id, product_name, product_image, product_description, product_link, is_show, sort_order, create_by, create_time, update_by, update_time, remark FROM main_product_temp;

-- 复制typical_customer_temp到typical_customer
-- TRUNCATE TABLE typical_customer;
-- INSERT INTO typical_customer (customer_id, customer_name, customer_logo, customer_description, customer_link, is_show, sort_order, create_by, create_time, update_by, update_time, remark)
-- SELECT customer_id, customer_name, customer_logo, customer_description, customer_link, is_show, sort_order, create_by, create_time, update_by, update_time, remark FROM typical_customer_temp;

-- ----------------------------
-- 临时表清空操作
-- ----------------------------
-- TRUNCATE TABLE carousel_image_temp;
-- TRUNCATE TABLE main_product_temp;
-- TRUNCATE TABLE typical_customer_temp;

-- ----------------------------
-- 正式表清空操作  
-- ----------------------------
-- TRUNCATE TABLE carousel_image;
-- TRUNCATE TABLE main_product;
-- TRUNCATE TABLE typical_customer;

-- ----------------------------
-- 示例数据
-- ----------------------------
-- INSERT INTO carousel_image_temp (image_url, is_show, sort_order, title, description, jump_url, create_by, create_time) VALUES
-- ('https://example.com/banner1.jpg', '1', 1, '横幅1', '这是横幅1的描述', '/page1', 'admin', NOW()),
-- ('https://example.com/banner2.jpg', '1', 2, '横幅2', '这是横幅2的描述', '/page2', 'admin', NOW());

-- INSERT INTO main_product_temp (product_name, product_image, product_description, product_link, is_show, sort_order, create_by, create_time) VALUES
-- ('产品A', 'https://example.com/productA.jpg', '产品A的描述', '/productA', '1', 1, 'admin', NOW()),
-- ('产品B', 'https://example.com/productB.jpg', '产品B的描述', '/productB', '1', 2, 'admin', NOW());

-- INSERT INTO typical_customer_temp (customer_name, customer_logo, customer_description, customer_link, is_show, sort_order, create_by, create_time) VALUES
-- ('客户A', 'https://example.com/customerA.png', '客户A的描述', '/customerA', '1', 1, 'admin', NOW()),
-- ('客户B', 'https://example.com/customerB.png', '客户B的描述', '/customerB', '1', 2, 'admin', NOW());

-- ----------------------------
-- 索引优化（根据查询需求）
-- ----------------------------
-- CREATE INDEX idx_carousel_image_temp_is_show_sort ON carousel_image_temp(is_show, sort_order);
-- CREATE INDEX idx_main_product_temp_is_show_sort ON main_product_temp(is_show, sort_order);
-- CREATE INDEX idx_typical_customer_temp_is_show_sort ON typical_customer_temp(is_show, sort_order);

-- ----------------------------
-- 注释说明
-- ----------------------------
-- 临时表(carousel_image_temp, main_product_temp, typical_customer_temp)用于保存操作
-- 正式表(carousel_image, main_product, typical_customer)用于展示操作
-- 预览操作从临时表读取数据，可以实时查看配置效果
-- 发布操作会将临时表数据完整复制到正式表，覆盖正式表的所有数据
-- 生产环境从正式表读取数据，确保展示数据的稳定性