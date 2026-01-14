-- 微信公众号图文消息图片表
DROP TABLE IF EXISTS `txwx_graphic_information_image`;
CREATE TABLE `txwx_graphic_information_image` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `media_id` varchar(100) NOT NULL COMMENT '媒体ID',
  `name` varchar(255) DEFAULT NULL COMMENT '图片名称',
  `url` varchar(500) NOT NULL COMMENT '图片URL',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0代表存在 1代表删除)',
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
  UNIQUE KEY `uk_media_id` (`media_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微信公众号图文消息图片表';
