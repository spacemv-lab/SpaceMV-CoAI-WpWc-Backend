-- SpaceMV问道内容创作发布改造第一阶段数据库脚本
-- Target: YOUR_MYSQL_HOST:3306/ry-cloud2
-- Scope:
--   1. Confirm target database and existing WeChat article table.
--   2. Create content_article, content_publish_job, content_publish_result.
--   3. Insert one SITE_WENDAO smoke-test article/job/result.
--   4. Verify required indexes and slug uniqueness.

USE `ry-cloud2`;

SELECT DATABASE() AS current_database;
SHOW TABLES LIKE 'txwx_article';
SHOW TABLES LIKE 'content_%';

DELIMITER $$

DROP PROCEDURE IF EXISTS content_phase1_assert_preconditions$$
CREATE PROCEDURE content_phase1_assert_preconditions()
BEGIN
  IF DATABASE() <> 'ry-cloud2' THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'Unexpected database. Expected ry-cloud2.';
  END IF;

  IF NOT EXISTS (
    SELECT 1
    FROM information_schema.tables
    WHERE table_schema = DATABASE()
      AND table_name = 'txwx_article'
  ) THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'Required legacy table txwx_article does not exist.';
  END IF;
END$$

CALL content_phase1_assert_preconditions()$$
DROP PROCEDURE IF EXISTS content_phase1_assert_preconditions$$

DELIMITER ;

CREATE TABLE IF NOT EXISTS content_article (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  title VARCHAR(255) NOT NULL COMMENT '文章标题',
  slug VARCHAR(255) NOT NULL COMMENT '文章访问标识',
  summary VARCHAR(1000) DEFAULT NULL COMMENT '文章摘要',
  cover_url VARCHAR(1000) DEFAULT NULL COMMENT '封面图URL',
  author_id BIGINT DEFAULT NULL COMMENT '作者ID',
  author_name VARCHAR(100) DEFAULT NULL COMMENT '作者名称',

  content_html LONGTEXT DEFAULT NULL COMMENT 'HTML正文',
  content_markdown LONGTEXT DEFAULT NULL COMMENT 'Markdown正文',
  content_json LONGTEXT DEFAULT NULL COMMENT '结构化正文JSON',

  status VARCHAR(32) NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT草稿 REVIEWING审核中 PUBLISHED已发布 OFFLINE已下架',
  visibility VARCHAR(32) NOT NULL DEFAULT 'PUBLIC' COMMENT '可见性：PUBLIC公开 LOGIN登录可见 PAID付费可见',
  is_paid CHAR(1) NOT NULL DEFAULT '0' COMMENT '是否付费文章：0否 1是',

  seo_title VARCHAR(255) DEFAULT NULL COMMENT 'SEO标题',
  seo_description VARCHAR(500) DEFAULT NULL COMMENT 'SEO描述',
  seo_keywords VARCHAR(500) DEFAULT NULL COMMENT 'SEO关键词',
  canonical_url VARCHAR(1000) DEFAULT NULL COMMENT '规范URL',

  published_at DATETIME DEFAULT NULL COMMENT '发布时间',
  create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  del_flag CHAR(1) NOT NULL DEFAULT '0' COMMENT '删除标志：0存在 1删除',

  PRIMARY KEY (id),
  UNIQUE KEY uk_content_article_slug (slug),
  KEY idx_content_article_status (status),
  KEY idx_content_article_published_at (published_at),
  KEY idx_content_article_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容中心-文章主表';

CREATE TABLE IF NOT EXISTS content_publish_job (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '发布任务ID',
  article_id BIGINT NOT NULL COMMENT '文章ID',
  target_code VARCHAR(64) NOT NULL COMMENT '发布目标编码：SITE_WENDAO WECHAT_OFFICIAL_ACCOUNT等',
  status VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING PROCESSING SUCCESS FAILED CANCELLED',
  scheduled_at DATETIME DEFAULT NULL COMMENT '计划发布时间',
  started_at DATETIME DEFAULT NULL COMMENT '开始时间',
  finished_at DATETIME DEFAULT NULL COMMENT '结束时间',

  operator_id BIGINT DEFAULT NULL COMMENT '操作人ID',
  operator_name VARCHAR(100) DEFAULT NULL COMMENT '操作人名称',
  error_message TEXT DEFAULT NULL COMMENT '失败原因',

  create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  del_flag CHAR(1) NOT NULL DEFAULT '0' COMMENT '删除标志：0存在 1删除',

  PRIMARY KEY (id),
  KEY idx_publish_job_article_id (article_id),
  KEY idx_publish_job_target_status (target_code, status),
  KEY idx_publish_job_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容中心-发布任务表';

CREATE TABLE IF NOT EXISTS content_publish_result (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '发布结果ID',
  job_id BIGINT NOT NULL COMMENT '发布任务ID',
  article_id BIGINT NOT NULL COMMENT '文章ID',
  target_code VARCHAR(64) NOT NULL COMMENT '发布目标编码',
  external_id VARCHAR(255) DEFAULT NULL COMMENT '外部平台ID',
  external_url VARCHAR(1000) DEFAULT NULL COMMENT '外部平台URL',
  site_url VARCHAR(1000) DEFAULT NULL COMMENT '站内URL',
  payload_json LONGTEXT DEFAULT NULL COMMENT '发布请求快照JSON',
  result_json LONGTEXT DEFAULT NULL COMMENT '发布结果JSON',

  create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  del_flag CHAR(1) NOT NULL DEFAULT '0' COMMENT '删除标志：0存在 1删除',

  PRIMARY KEY (id),
  KEY idx_publish_result_job_id (job_id),
  KEY idx_publish_result_article_id (article_id),
  KEY idx_publish_result_target_code (target_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容中心-发布结果表';

DELIMITER $$

DROP PROCEDURE IF EXISTS content_phase1_assert_schema$$
CREATE PROCEDURE content_phase1_assert_schema()
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = DATABASE() AND table_name = 'content_article'
  ) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'content_article was not created.';
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = DATABASE() AND table_name = 'content_publish_job'
  ) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'content_publish_job was not created.';
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = DATABASE() AND table_name = 'content_publish_result'
  ) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'content_publish_result was not created.';
  END IF;

  IF NOT EXISTS (
    SELECT 1
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'content_article'
      AND index_name = 'uk_content_article_slug'
      AND non_unique = 0
      AND column_name = 'slug'
  ) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Missing unique index uk_content_article_slug.';
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM (
      SELECT index_name, GROUP_CONCAT(column_name ORDER BY seq_in_index) AS indexed_columns
      FROM information_schema.statistics
      WHERE table_schema = DATABASE()
        AND table_name = 'content_article'
      GROUP BY index_name
    ) s
    WHERE s.index_name = 'idx_content_article_status'
      AND s.indexed_columns = 'status'
  ) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Missing index idx_content_article_status.';
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM (
      SELECT index_name, GROUP_CONCAT(column_name ORDER BY seq_in_index) AS indexed_columns
      FROM information_schema.statistics
      WHERE table_schema = DATABASE()
        AND table_name = 'content_article'
      GROUP BY index_name
    ) s
    WHERE s.index_name = 'idx_content_article_published_at'
      AND s.indexed_columns = 'published_at'
  ) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Missing index idx_content_article_published_at.';
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM (
      SELECT index_name, GROUP_CONCAT(column_name ORDER BY seq_in_index) AS indexed_columns
      FROM information_schema.statistics
      WHERE table_schema = DATABASE()
        AND table_name = 'content_article'
      GROUP BY index_name
    ) s
    WHERE s.index_name = 'idx_content_article_create_time'
      AND s.indexed_columns = 'create_time'
  ) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Missing index idx_content_article_create_time.';
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM (
      SELECT index_name, GROUP_CONCAT(column_name ORDER BY seq_in_index) AS indexed_columns
      FROM information_schema.statistics
      WHERE table_schema = DATABASE()
        AND table_name = 'content_publish_job'
      GROUP BY index_name
    ) s
    WHERE s.index_name = 'idx_publish_job_article_id'
      AND s.indexed_columns = 'article_id'
  ) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Missing index idx_publish_job_article_id.';
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM (
      SELECT index_name, GROUP_CONCAT(column_name ORDER BY seq_in_index) AS indexed_columns
      FROM information_schema.statistics
      WHERE table_schema = DATABASE()
        AND table_name = 'content_publish_job'
      GROUP BY index_name
    ) s
    WHERE s.index_name = 'idx_publish_job_target_status'
      AND s.indexed_columns = 'target_code,status'
  ) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Missing index idx_publish_job_target_status.';
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM (
      SELECT index_name, GROUP_CONCAT(column_name ORDER BY seq_in_index) AS indexed_columns
      FROM information_schema.statistics
      WHERE table_schema = DATABASE()
        AND table_name = 'content_publish_job'
      GROUP BY index_name
    ) s
    WHERE s.index_name = 'idx_publish_job_create_time'
      AND s.indexed_columns = 'create_time'
  ) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Missing index idx_publish_job_create_time.';
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM (
      SELECT index_name, GROUP_CONCAT(column_name ORDER BY seq_in_index) AS indexed_columns
      FROM information_schema.statistics
      WHERE table_schema = DATABASE()
        AND table_name = 'content_publish_result'
      GROUP BY index_name
    ) s
    WHERE s.index_name = 'idx_publish_result_job_id'
      AND s.indexed_columns = 'job_id'
  ) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Missing index idx_publish_result_job_id.';
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM (
      SELECT index_name, GROUP_CONCAT(column_name ORDER BY seq_in_index) AS indexed_columns
      FROM information_schema.statistics
      WHERE table_schema = DATABASE()
        AND table_name = 'content_publish_result'
      GROUP BY index_name
    ) s
    WHERE s.index_name = 'idx_publish_result_article_id'
      AND s.indexed_columns = 'article_id'
  ) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Missing index idx_publish_result_article_id.';
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM (
      SELECT index_name, GROUP_CONCAT(column_name ORDER BY seq_in_index) AS indexed_columns
      FROM information_schema.statistics
      WHERE table_schema = DATABASE()
        AND table_name = 'content_publish_result'
      GROUP BY index_name
    ) s
    WHERE s.index_name = 'idx_publish_result_target_code'
      AND s.indexed_columns = 'target_code'
  ) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Missing index idx_publish_result_target_code.';
  END IF;
END$$

CALL content_phase1_assert_schema()$$
DROP PROCEDURE IF EXISTS content_phase1_assert_schema$$

DELIMITER ;

SET @phase1_article_slug := 'phase1-smoke-20260610';
SET @phase1_site_url := CONCAT('/blog/', @phase1_article_slug);

INSERT INTO content_article (
  title,
  slug,
  summary,
  cover_url,
  author_id,
  author_name,
  content_html,
  content_markdown,
  content_json,
  status,
  visibility,
  is_paid,
  seo_title,
  seo_description,
  seo_keywords,
  canonical_url,
  published_at,
  create_by,
  update_by,
  remark,
  del_flag
) VALUES (
  'Phase 1 Database Smoke Test',
  @phase1_article_slug,
  'Smoke-test article for the SpaceMV Wendao content phase 1 database schema.',
  NULL,
  NULL,
  'Codex',
  '<p>Phase 1 database smoke test.</p><p>{{chart:us-cpi-core}}</p>',
  'Phase 1 database smoke test. {{chart:us-cpi-core}}',
  '{"type":"doc","source":"phase1-smoke","charts":["us-cpi-core"]}',
  'PUBLISHED',
  'PUBLIC',
  '0',
  'Phase 1 Database Smoke Test',
  'Smoke-test article for validating SpaceMV Wendao content phase 1 database tasks.',
  'SpaceMV,Wendao,content,phase1',
  @phase1_site_url,
  NOW(),
  'codex',
  'codex',
  'phase1 database smoke test',
  '0'
) ON DUPLICATE KEY UPDATE
  id = LAST_INSERT_ID(id),
  title = VALUES(title),
  summary = VALUES(summary),
  content_html = VALUES(content_html),
  content_markdown = VALUES(content_markdown),
  content_json = VALUES(content_json),
  status = VALUES(status),
  visibility = VALUES(visibility),
  is_paid = VALUES(is_paid),
  seo_title = VALUES(seo_title),
  seo_description = VALUES(seo_description),
  seo_keywords = VALUES(seo_keywords),
  canonical_url = VALUES(canonical_url),
  published_at = VALUES(published_at),
  update_by = VALUES(update_by),
  remark = VALUES(remark),
  del_flag = VALUES(del_flag);

SET @phase1_article_id := LAST_INSERT_ID();
SET @phase1_job_id := NULL;

SELECT id INTO @phase1_job_id
FROM content_publish_job
WHERE article_id = @phase1_article_id
  AND target_code = 'SITE_WENDAO'
  AND remark = 'phase1 database smoke test'
ORDER BY id
LIMIT 1;

INSERT INTO content_publish_job (
  article_id,
  target_code,
  status,
  scheduled_at,
  started_at,
  finished_at,
  operator_id,
  operator_name,
  error_message,
  create_by,
  update_by,
  remark,
  del_flag
)
SELECT
  @phase1_article_id,
  'SITE_WENDAO',
  'SUCCESS',
  NULL,
  NOW(),
  NOW(),
  NULL,
  'Codex',
  NULL,
  'codex',
  'codex',
  'phase1 database smoke test',
  '0'
WHERE @phase1_job_id IS NULL;

SET @phase1_job_id := COALESCE(@phase1_job_id, LAST_INSERT_ID());

UPDATE content_publish_job
SET status = 'SUCCESS',
    started_at = COALESCE(started_at, NOW()),
    finished_at = NOW(),
    operator_name = 'Codex',
    error_message = NULL,
    update_by = 'codex',
    del_flag = '0'
WHERE id = @phase1_job_id;

SET @phase1_result_id := NULL;

SELECT id INTO @phase1_result_id
FROM content_publish_result
WHERE job_id = @phase1_job_id
  AND article_id = @phase1_article_id
  AND target_code = 'SITE_WENDAO'
  AND remark = 'phase1 database smoke test'
ORDER BY id
LIMIT 1;

INSERT INTO content_publish_result (
  job_id,
  article_id,
  target_code,
  external_id,
  external_url,
  site_url,
  payload_json,
  result_json,
  create_by,
  update_by,
  remark,
  del_flag
)
SELECT
  @phase1_job_id,
  @phase1_article_id,
  'SITE_WENDAO',
  NULL,
  NULL,
  @phase1_site_url,
  '{"targetCode":"SITE_WENDAO","source":"phase1-smoke"}',
  '{"status":"SUCCESS","siteUrl":"/blog/phase1-smoke-20260610"}',
  'codex',
  'codex',
  'phase1 database smoke test',
  '0'
WHERE @phase1_result_id IS NULL;

SET @phase1_result_id := COALESCE(@phase1_result_id, LAST_INSERT_ID());

UPDATE content_publish_result
SET site_url = @phase1_site_url,
    payload_json = '{"targetCode":"SITE_WENDAO","source":"phase1-smoke"}',
    result_json = '{"status":"SUCCESS","siteUrl":"/blog/phase1-smoke-20260610"}',
    update_by = 'codex',
    del_flag = '0'
WHERE id = @phase1_result_id;

DELIMITER $$

DROP PROCEDURE IF EXISTS content_phase1_assert_slug_unique$$
CREATE PROCEDURE content_phase1_assert_slug_unique()
BEGIN
  DECLARE duplicate_seen TINYINT DEFAULT 0;
  DECLARE inserted_id BIGINT DEFAULT NULL;
  DECLARE CONTINUE HANDLER FOR 1062 SET duplicate_seen = 1;

  INSERT INTO content_article (
    title,
    slug,
    status,
    visibility,
    is_paid,
    create_by,
    update_by,
    remark,
    del_flag
  ) VALUES (
    'Phase 1 Duplicate Slug Check',
    @phase1_article_slug,
    'DRAFT',
    'PUBLIC',
    '0',
    'codex',
    'codex',
    'phase1 duplicate slug check',
    '0'
  );

  SET inserted_id = LAST_INSERT_ID();

  IF duplicate_seen = 0 THEN
    DELETE FROM content_article WHERE id = inserted_id;
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'uk_content_article_slug did not reject a duplicate slug.';
  END IF;
END$$

CALL content_phase1_assert_slug_unique()$$
DROP PROCEDURE IF EXISTS content_phase1_assert_slug_unique$$

DELIMITER ;

SELECT
  a.id AS article_id,
  a.slug,
  a.status AS article_status,
  j.id AS job_id,
  j.target_code,
  j.status AS job_status,
  r.id AS result_id,
  r.site_url
FROM content_article a
JOIN content_publish_job j ON j.article_id = a.id
JOIN content_publish_result r ON r.job_id = j.id AND r.article_id = a.id
WHERE a.slug = @phase1_article_slug
  AND j.target_code = 'SITE_WENDAO'
  AND r.target_code = 'SITE_WENDAO';

SELECT
  table_name,
  index_name,
  non_unique,
  GROUP_CONCAT(column_name ORDER BY seq_in_index) AS indexed_columns
FROM information_schema.statistics
WHERE table_schema = DATABASE()
  AND table_name IN ('content_article', 'content_publish_job', 'content_publish_result')
GROUP BY table_name, index_name, non_unique
ORDER BY table_name, index_name;

-- Optional smoke-test cleanup, only for the deterministic row inserted above.
-- DELETE r FROM content_publish_result r
-- JOIN content_article a ON a.id = r.article_id
-- WHERE a.slug = 'phase1-smoke-20260610';
-- DELETE j FROM content_publish_job j
-- JOIN content_article a ON a.id = j.article_id
-- WHERE a.slug = 'phase1-smoke-20260610';
-- DELETE FROM content_article WHERE slug = 'phase1-smoke-20260610';
