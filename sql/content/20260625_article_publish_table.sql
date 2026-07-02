-- 多平台发布设计 v3: article_publish 表
-- 合并 content_publish_job + content_publish_result 功能为单表
-- 保留旧表不动，用于历史追溯
-- Target: YOUR_MYSQL_HOST:3306/ry-cloud2

USE `ry-cloud2`;

CREATE TABLE IF NOT EXISTS article_publish (
  id                      BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',

  article_id              BIGINT       NOT NULL COMMENT 'FK → content_article.id',
  platform                VARCHAR(50)  NOT NULL COMMENT '平台编码: SITE_WENDAO / WECHAT_OFFICIAL_ACCOUNT / ZHISHU_XINGQIU ...',

  -- 平台特有信息
  platform_title          VARCHAR(200) DEFAULT NULL COMMENT '该平台标题（NULL=使用主标题）',
  platform_tags           VARCHAR(500) DEFAULT NULL COMMENT '该平台标签',
  style_preset            VARCHAR(100) DEFAULT NULL COMMENT '发布时使用的样式预设名',

  -- 发布状态: PENDING / PUBLISHING / SUCCESS / FAILED / DRAFT_CREATED / DELETED
  status                  VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT '发布状态',

  -- 内容快照（发出去时的 contentJson，仅用于历史预览）
  content_json_snapshot   LONGTEXT     COMMENT '发出去时的 contentJson 快照',

  -- 平台返回信息
  platform_article_id     VARCHAR(200) DEFAULT NULL COMMENT '平台上的文章 ID',
  platform_url            VARCHAR(500) DEFAULT NULL COMMENT '平台上的链接',

  -- 错误与重试
  error_code              VARCHAR(100) DEFAULT NULL COMMENT '错误码',
  error_message           TEXT         DEFAULT NULL COMMENT '错误描述',
  retry_count             INT          DEFAULT 0 COMMENT '重试次数',

  -- 有效标记: 1=有效 0=已从该平台删除
  is_active               CHAR(1)      DEFAULT '1' COMMENT '有效标记',

  -- 时间
  published_at            DATETIME     DEFAULT NULL COMMENT '成功发布时间',
  create_by               VARCHAR(64)  DEFAULT '' COMMENT '创建人',
  create_time             DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by               VARCHAR(64)  DEFAULT '' COMMENT '更新人',
  update_time             DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  remark                  VARCHAR(500) DEFAULT NULL COMMENT '备注',
  del_flag                CHAR(1)      DEFAULT '0' COMMENT '删除标志',

  UNIQUE KEY uk_article_platform (article_id, platform, del_flag),
  KEY idx_article_id (article_id),
  KEY idx_platform_status (platform, status),
  KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章平台发布记录（多平台发布设计v3）';
