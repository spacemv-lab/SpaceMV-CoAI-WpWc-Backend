-- 修复 article_publish 表缺少的列（多平台发布v3表结构）
-- 新增 content_article 表微信草稿相关字段（保存草稿到微信功能）
-- Target: YOUR_MYSQL_HOST:3306/ry-cloud2

USE `ry-cloud2`;

ALTER TABLE article_publish
    ADD COLUMN create_by   VARCHAR(64)  DEFAULT '' COMMENT '创建人' AFTER published_at,
    ADD COLUMN create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER create_by,
    ADD COLUMN update_by   VARCHAR(64)  DEFAULT '' COMMENT '更新人' AFTER create_time,
    ADD COLUMN update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER update_by;

ALTER TABLE content_article
    ADD COLUMN wechat_media_id   VARCHAR(500) DEFAULT '' COMMENT '微信公众号草稿 media_id' AFTER published_at,
    ADD COLUMN wechat_account_id BIGINT       DEFAULT NULL COMMENT '微信公众号账号ID' AFTER wechat_media_id;
