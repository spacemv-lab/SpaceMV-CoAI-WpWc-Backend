-- 新增 wen_dao_content_html 列：问道站点专用 HTML（不含图表图片，仅含交互短码）
-- 前端 buildSaveData() 会同时写入 contentHtml（含 img + 短码）和 wenDaoContentHtml（仅短码）
-- 后端 selectWendaoSiteBySlug 使用 coalesce(wen_dao_content_html, content_html) 作为 content_html 返回
-- Target: YOUR_MYSQL_HOST:3306/ry-cloud2

USE `ry-cloud2`;

ALTER TABLE content_article
    ADD COLUMN wen_dao_content_html LONGTEXT DEFAULT NULL COMMENT '问道站点专用HTML正文（不含图表图片）' AFTER content_html;
