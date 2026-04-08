# 数据库设计文档

本文档说明多租户自媒体渠道平台的数据库表设计，分为 MySQL 和 ClickHouse 两部分。

---

## 一、MySQL 数据库设计

### 1.1 系统表（复用若依原表）

| 表名 | 说明 |
|------|------|
| sys_user | 用户表 |
| sys_role | 角色表 |
| sys_menu | 菜单表 |
| sys_dept | 部门表 |
| sys_dict_type | 字典类型表 |
| sys_dict_data | 字典数据表 |

### 1.2 新增 MySQL 表

#### 1.2.1 用户协作权限表

**表名**: `txwx_user_permission`

**说明**: 用户与角色/产品/渠道的权限关联表，支持多级权限配置

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| relation_type | INT | 关联类型：0-系统级 1-产品级 2-渠道级 |
| relation_ids | VARCHAR(500) | 关联ID列表（JSON数组） |
| create_by | VARCHAR(64) | 创建人 |
| create_time | DATETIME | 创建时间 |

**建表语句**：
```sql
CREATE TABLE `txwx_user_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `relation_type` INT NOT NULL COMMENT '关联类型：0-系统级 1-产品级 2-渠道级',
  `relation_ids` VARCHAR(500) NOT NULL COMMENT '关联ID列表(JSON数组)',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建人',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_relation_type` (`relation_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户协作权限分配表';
```

#### 1.2.2 产品管理表

**表名**: `txwx_product`

**说明**: 产品信息表

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 所属用户ID |
| product_name | VARCHAR(100) | 产品名称 |
| product_code | VARCHAR(50) | 产品编码 |
| description | VARCHAR(500) | 产品描述 |
| status | CHAR(1) | 状态：0-停用 1-启用 |
| create_by | VARCHAR(64) | 创建人 |
| create_time | DATETIME | 创建时间 |
| update_by | VARCHAR(64) | 更新人 |
| update_time | DATETIME | 更新时间 |

**建表语句**：
```sql
CREATE TABLE `txwx_product` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '所属用户ID',
  `product_name` VARCHAR(100) NOT NULL COMMENT '产品名称',
  `product_code` VARCHAR(50) NOT NULL COMMENT '产品编码',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '产品描述',
  `status` CHAR(1) DEFAULT '1' COMMENT '状态：0-停用 1-启用',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建人',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新人',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_code` (`product_code`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品信息表';
```

#### 1.2.3 渠道管理表

**表名**: `txwx_channel`

**说明**: 渠道元数据表

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键 |
| channel_code | VARCHAR(50) | 渠道编码 |
| channel_name | VARCHAR(100) | 渠道名称 |
| channel_type | VARCHAR(32) | 渠道类型：wechat/douyin/xiaohongshu |
| description | VARCHAR(500) | 渠道描述 |
| create_by | VARCHAR(64) | 创建人 |
| create_time | DATETIME | 创建时间 |

**建表语句**：
```sql
CREATE TABLE `txwx_channel` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `channel_code` VARCHAR(50) NOT NULL COMMENT '渠道编码',
  `channel_name` VARCHAR(100) NOT NULL COMMENT '渠道名称',
  `channel_type` VARCHAR(32) NOT NULL COMMENT '渠道类型：wechat/douyin/xiaohongshu',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '渠道描述',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建人',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_channel_code` (`channel_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='渠道元数据表';
```

#### 1.2.4 产品-渠道关联表

**表名**: `txwx_product_channel`

**说明**: 产品与渠道的多对多关联表

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键 |
| product_id | BIGINT | 产品ID |
| channel_id | BIGINT | 渠道ID |
| create_by | VARCHAR(64) | 创建人 |
| create_time | DATETIME | 创建时间 |

**建表语句**：
```sql
CREATE TABLE `txwx_product_channel` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `product_id` BIGINT NOT NULL COMMENT '产品ID',
  `channel_id` BIGINT NOT NULL COMMENT '渠道ID',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建人',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_channel_id` (`channel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品-渠道关联表';
```

#### 1.2.5 账号管理表

**表名**: `txwx_account`

**说明**: 自媒体账号表

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键 |
| channel_id | BIGINT | 渠道ID |
| product_id | BIGINT | 产品ID |
| account_name | VARCHAR(100) | 账号名称 |
| account_no | VARCHAR(100) | 账号号码 |
| appid | VARCHAR(255) | 微信appid |
| secret | VARCHAR(255) | 微信secret |
| token | VARCHAR(255) | 令牌 |
| encoding_aes_key | VARCHAR(255) | 消息加密码 |
| status | CHAR(1) | 状态：0-停用 1-启用 |
| last_sync_time | DATETIME | 最后同步时间 |
| create_by | VARCHAR(64) | 创建人 |
| create_time | DATETIME | 创建时间 |
| update_by | VARCHAR(64) | 更新人 |
| update_time | DATETIME | 更新时间 |

**建表语句**：
```sql
CREATE TABLE `txwx_account` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `channel_id` BIGINT NOT NULL COMMENT '渠道ID',
  `product_id` BIGINT NOT NULL COMMENT '产品ID',
  `account_name` VARCHAR(100) NOT NULL COMMENT '账号名称',
  `account_no` VARCHAR(100) DEFAULT NULL COMMENT '账号号码',
  `appid` VARCHAR(255) DEFAULT NULL COMMENT '微信appid',
  `secret` VARCHAR(255) DEFAULT NULL COMMENT '微信secret',
  `token` VARCHAR(255) DEFAULT NULL COMMENT '令牌',
  `encoding_aes_key` VARCHAR(255) DEFAULT NULL COMMENT '消息加密码',
  `status` CHAR(1) DEFAULT '1' COMMENT '状态：0-停用 1-启用',
  `last_sync_time` DATETIME DEFAULT NULL COMMENT '最后同步时间',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建人',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新人',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_channel_id` (`channel_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自媒体账号表';
```

#### 1.2.6 改造的现有表

**表名**: `txwx_article`

**新增字段**: `account_id` (BIGINT) - 账号ID

**ALTER TABLE 语句**：
```sql
ALTER TABLE `txwx_article` 
ADD COLUMN `account_id` BIGINT NULL COMMENT '账号ID' AFTER `product_id`;

-- 添加索引
ALTER TABLE `txwx_article` 
ADD INDEX `idx_account_id` (`account_id`);
```

**表名**: `txwx_graphic_information_image`

**新增字段**: `account_id` (BIGINT) - 账号ID

**ALTER TABLE 语句**：
```sql
ALTER TABLE `txwx_graphic_information_image` 
ADD COLUMN `account_id` BIGINT NULL COMMENT '账号ID' AFTER `product_id`;

-- 添加索引
ALTER TABLE `txwx_graphic_information_image` 
ADD INDEX `idx_account_id` (`account_id`);
```

---

## 二、ClickHouse 数据库设计

### 2.1 表结构说明

**注意**: 以下表结构基于 ClickHouse 实际结构，**所有表都需要添加 `account_id` 字段**。

**命名规范**:
- **ods_***: ODS 层（原始数据层）
- **dws_***: DWS 层（汇总数据层）
- **dwd_***: DWD 层（明细数据层）
- **dim_***: DIM 层（维度数据层）

### 2.2 ODS 层表（原始数据层）

#### 2.2.1 ods_users

**说明**: 用户增减数据

| 字段名 | 类型 | 说明 |
|--------|------|------|
| account_id | Nullable(Int64) | 账号ID |
| ref_date | Date | 数据日期 |
| user_source | UInt16 | 用户来源 |
| new_user | UInt16 | 新增用户 |
| cancel_user | UInt16 | 取消关注用户 |

**建表语句**：
```sql
CREATE TABLE wcai.ods_users (
    `account_id` Nullable(Int64) COMMENT '账号ID',
    `ref_date` Date,
    `user_source` UInt16,
    `new_user` UInt16,
    `cancel_user` UInt16
)
ENGINE = ReplacingMergeTree
PARTITION BY toYYYYMMDD(ref_date)
ORDER BY (ref_date, user_source, new_user, cancel_user)
SETTINGS index_granularity = 8192
```

**ALTER TABLE 语句**（为现有表添加字段）：
```sql
ALTER TABLE wcai.ods_users 
ADD COLUMN IF NOT EXISTS account_id Nullable(Int64);
```

#### 2.2.2 ods_article

**说明**: 已发布文章

| 字段名 | 类型 | 说明 |
|--------|------|------|
| account_id | Nullable(Int64) | 账号ID |
| msgid | String | 文章唯一标识 |
| title | String | 文章标题 |
| create_time | DateTime | 发布时间 |
| author | String | 作者 |
| url | String | 文章链接 |

**建表语句**：
```sql
CREATE TABLE wcai.ods_article (
    `account_id` Nullable(Int64) COMMENT '账号ID',
    `msgid` String COMMENT '文章唯一标识(mid_idx)',
    `title` String COMMENT '文章标题',
    `create_time` DateTime COMMENT '文章发布时间',
    `author` String COMMENT '作者',
    `url` String COMMENT '文章链接'
)
ENGINE = ReplacingMergeTree
PARTITION BY toYYYYMM(create_time)
ORDER BY (create_time, msgid)
SETTINGS index_granularity = 8192
```

**ALTER TABLE 语句**：
```sql
ALTER TABLE wcai.ods_article 
ADD COLUMN IF NOT EXISTS account_id Nullable(Int64);
```

#### 2.2.3 ods_article_read_daily

**说明**: 文章阅读数据

| 字段名 | 类型 | 说明 |
|--------|------|------|
| account_id | Nullable(Int64) | 账号ID |
| ref_date | Date | 数据日期 |
| msgid | String | 图文消息ID |
| read_user_total | UInt32 | 图文页阅读人数 |
| read_user_source_all | UInt32 | 全部来源阅读人数 |
| read_user_source_msg | UInt32 | 会话阅读人数 |
| read_user_source_chat | UInt32 | 好友转发阅读人数 |
| read_user_source_moments | UInt32 | 朋友圈阅读人数 |
| read_user_source_homepage | UInt32 | 公众号主页阅读人数 |
| read_user_source_other | UInt32 | 其他场景阅读人数 |
| read_user_source_recommend | UInt32 | 推荐阅读人数 |
| read_user_source_search | UInt32 | 搜索阅读人数 |
| pull_time | DateTime | 数据拉取/入库时间 |

**建表语句**：
```sql
CREATE TABLE wcai.ods_article_read_daily (
    `account_id` Nullable(Int64) COMMENT '账号ID',
    `ref_date` Date COMMENT '数据日期',
    `msgid` String COMMENT '图文消息id',
    `read_user_total` UInt32 COMMENT '图文页阅读人数',
    `read_user_source_all` UInt32 COMMENT '全部来源阅读人数',
    `read_user_source_msg` UInt32 COMMENT '会话阅读人数',
    `read_user_source_chat` UInt32 COMMENT '好友转发阅读人数',
    `read_user_source_moments` UInt32 COMMENT '朋友圈阅读人数',
    `read_user_source_homepage` UInt32 COMMENT '公众号主页阅读人数',
    `read_user_source_other` UInt32 COMMENT '其他场景阅读人数',
    `read_user_source_recommend` UInt32 COMMENT '推荐阅读人数',
    `read_user_source_search` UInt32 COMMENT '搜索阅读人数',
    `pull_time` DateTime DEFAULT now() COMMENT '数据拉取/入库时间'
)
ENGINE = ReplacingMergeTree(pull_time)
PARTITION BY toYYYYMM(ref_date)
ORDER BY (ref_date, msgid)
SETTINGS index_granularity = 8192
```

**ALTER TABLE 语句**：
```sql
ALTER TABLE wcai.ods_article_read_daily 
ADD COLUMN IF NOT EXISTS account_id Nullable(Int64);
```

#### 2.2.4 ods_article_share_daily

**说明**: 文章分享数据

| 字段名 | 类型 | 说明 |
|--------|------|------|
| account_id | Nullable(Int64) | 账号ID |
| ref_date | Date | 数据日期 |
| msgid | String | 图文消息ID |
| share_user | UInt32 | 分享人数 |
| pull_time | DateTime | 数据拉取/入库时间 |

**建表语句**：
```sql
CREATE TABLE wcai.ods_article_share_daily (
    `account_id` Nullable(Int64) COMMENT '账号ID',
    `ref_date` Date,
    `msgid` String,
    `share_user` UInt32,
    `pull_time` DateTime DEFAULT now() COMMENT '数据拉取/入库时间'
)
ENGINE = ReplacingMergeTree(pull_time)
PARTITION BY toYYYYMM(ref_date)
ORDER BY (ref_date, msgid)
SETTINGS index_granularity = 8192
```

**ALTER TABLE 语句**：
```sql
ALTER TABLE wcai.ods_article_share_daily 
ADD COLUMN IF NOT EXISTS account_id Nullable(Int64);
```

#### 2.2.5 ods_article_summary_daily

**说明**: 文章概况数据

| 字段名 | 类型 | 说明 |
|--------|------|------|
| account_id | Nullable(Int64) | 账号ID |
| ref_date | Date | 数据日期 |
| read_user_total | UInt32 | 阅读人数 |
| read_user_source_all | UInt32 | 全部来源阅读人数 |
| read_user_source_msg | UInt32 | 会话阅读人数 |
| read_user_source_chat | UInt32 | 好友转发阅读人数 |
| read_user_source_moments | UInt32 | 朋友圈阅读人数 |
| read_user_source_homepage | UInt32 | 公众号主页阅读人数 |
| read_user_source_other | UInt32 | 其他场景阅读人数 |
| read_user_source_recommend | UInt32 | 推荐阅读人数 |
| read_user_source_search | UInt32 | 搜索阅读人数 |
| share_user | UInt32 | 分享人数 |
| zaikan_user | UInt32 | 在看人数 |
| like_user | UInt32 | 点赞人数 |
| comment_count | UInt32 | 评论数 |
| collection_user | UInt32 | 收藏人数 |
| redirect_ori_page_user | UInt32 | 阅读原文人数 |
| send_page_count | UInt32 | 群发篇数 |
| pull_time | DateTime | 数据拉取/入库时间 |

**建表语句**：
```sql
CREATE TABLE wcai.ods_article_summary_daily (
    `account_id` Nullable(Int64) COMMENT '账号ID',
    `ref_date` Date,
    `read_user_total` UInt32,
    `read_user_source_all` UInt32,
    `read_user_source_msg` UInt32,
    `read_user_source_chat` UInt32,
    `read_user_source_moments` UInt32,
    `read_user_source_homepage` UInt32,
    `read_user_source_other` UInt32,
    `read_user_source_recommend` UInt32,
    `read_user_source_search` UInt32,
    `share_user` UInt32,
    `zaikan_user` UInt32,
    `like_user` UInt32,
    `comment_count` UInt32,
    `collection_user` UInt32,
    `redirect_ori_page_user` UInt32,
    `send_page_count` UInt32,
    `pull_time` DateTime DEFAULT now() COMMENT '数据拉取/入库时间'
)
ENGINE = ReplacingMergeTree(pull_time)
PARTITION BY toYYYYMM(ref_date)
ORDER BY ref_date
SETTINGS index_granularity = 8192
```

**ALTER TABLE 语句**：
```sql
ALTER TABLE wcai.ods_article_summary_daily 
ADD COLUMN IF NOT EXISTS account_id Nullable(Int64);
```

#### 2.2.6 ods_article_detail_daily

**说明**: 文章详情数据

| 字段名 | 类型 | 说明 |
|--------|------|------|
| account_id | Nullable(Int64) | 账号ID |
| stat_date | Date | 统计日期 |
| ref_date | Date | 数据日期 |
| msgid | String | 图文消息ID |
| publish_type | UInt8 | 发布类型 |
| read_user | UInt32 | 阅读人数 |
| read_user_source_all | UInt32 | 全部来源阅读人数 |
| read_user_source_msg | UInt32 | 会话阅读人数 |
| read_user_source_chat | UInt32 | 好友转发阅读人数 |
| read_user_source_moments | UInt32 | 朋友圈阅读人数 |
| read_user_source_homepage | UInt32 | 公众号主页阅读人数 |
| read_user_source_other | UInt32 | 其他场景阅读人数 |
| read_user_source_recommend | UInt32 | 推荐阅读人数 |
| read_user_source_search | UInt32 | 搜索阅读人数 |
| share_user | UInt32 | 分享人数 |
| zaikan_user | UInt32 | 在看人数 |
| like_user | UInt32 | 点赞人数 |
| comment_count | UInt32 | 评论数 |
| collection_user | UInt32 | 收藏人数 |
| read_subscribe_user | UInt32 | 阅读关注人数 |
| read_delivery_rate | Float32 | 到达率 |
| read_finish_rate | Float32 | 完读率 |
| read_avg_activetime | Float32 | 平均阅读时长 |
| update_time | DateTime | 入库时间 |
| title | String | 标题 |
| url | String | 文章链接 |

**建表语句**：
```sql
CREATE TABLE wcai.ods_article_detail_daily (
    `account_id` Nullable(Int64) COMMENT '账号ID',
    `stat_date` Date,
    `ref_date` Date,
    `msgid` String,
    `publish_type` UInt8,
    `read_user` UInt32,
    `read_user_source_all` UInt32,
    `read_user_source_msg` UInt32,
    `read_user_source_chat` UInt32,
    `read_user_source_moments` UInt32,
    `read_user_source_homepage` UInt32,
    `read_user_source_other` UInt32,
    `read_user_source_recommend` UInt32,
    `read_user_source_search` UInt32,
    `share_user` UInt32,
    `zaikan_user` UInt32,
    `like_user` UInt32,
    `comment_count` UInt32,
    `collection_user` UInt32,
    `read_subscribe_user` UInt32,
    `read_delivery_rate` Float32,
    `read_finish_rate` Float32,
    `read_avg_activetime` Float32,
    `update_time` DateTime DEFAULT now() COMMENT '入库时间',
    `title` String,
    `url` String
)
ENGINE = ReplacingMergeTree(update_time)
PARTITION BY toYYYYMM(stat_date)
ORDER BY (stat_date, msgid)
SETTINGS index_granularity = 8192
```

**ALTER TABLE 语句**：
```sql
ALTER TABLE wcai.ods_article_detail_daily 
ADD COLUMN IF NOT EXISTS account_id Nullable(Int64);
```

### 2.3 DWD 层表（明细数据层）

#### 2.3.1 ods_article_read_by_source

**说明**: 文章阅读来源明细（原 article_perday）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| account_id | Nullable(Int64) | 账号ID |
| ref_date | Date | 数据日期 |
| user_source | UInt16 | 用户来源 |
| msgid | String | 图文消息ID |
| title | String | 标题 |
| int_page_read_user | UInt16 | 图文页阅读人数 |
| int_page_read_count | UInt16 | 图文页阅读次数 |
| ori_page_read_user | UInt16 | 原文页阅读人数 |
| ori_page_read_count | UInt16 | 原文页阅读次数 |
| share_user | UInt16 | 分享人数 |
| share_count | UInt16 | 分享次数 |
| add_to_fav_user | UInt16 | 收藏人数 |
| add_to_fav_count | UInt16 | 收藏次数 |

**建表语句**：
```sql
CREATE TABLE wcai.ods_article_read_by_source (
    `account_id` Nullable(Int64) COMMENT '账号ID',
    `ref_date` Date,
    `user_source` UInt16,
    `msgid` String,
    `title` String,
    `int_page_read_user` UInt16,
    `int_page_read_count` UInt16,
    `ori_page_read_user` UInt16,
    `ori_page_read_count` UInt16,
    `share_user` UInt16,
    `share_count` UInt16,
    `add_to_fav_user` UInt16,
    `add_to_fav_count` UInt16
)
ENGINE = MergeTree
PARTITION BY toYYYYMMDD(ref_date)
ORDER BY ref_date
SETTINGS index_granularity = 8192
```

**ALTER TABLE 语句**：
```sql
-- 先重命名原表
ALTER TABLE wcai.article_perday RENAME TO wcai.ods_article_read_by_source;

-- 添加 account_id 字段
ALTER TABLE wcai.ods_article_read_by_source 
ADD COLUMN IF NOT EXISTS account_id Nullable(Int64);
```

#### 2.3.2 ods_user_read_detail

**说明**: 用户阅读明细（原 user_read）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| account_id | Nullable(Int64) | 账号ID |
| ref_date | Date | 数据日期 |
| user_source | UInt64 | 用户来源 |
| int_page_read_user | UInt16 | 图文页阅读人数 |
| int_page_read_count | UInt16 | 图文页阅读次数 |
| ori_page_read_user | UInt16 | 原文页阅读人数 |
| ori_page_read_count | UInt16 | 原文页阅读次数 |
| share_user | UInt16 | 分享人数 |
| share_count | UInt16 | 分享次数 |
| add_to_fav_user | UInt16 | 收藏人数 |
| add_to_fav_count | UInt16 | 收藏次数 |

**建表语句**：
```sql
CREATE TABLE wcai.ods_user_read_detail (
    `account_id` Nullable(Int64) COMMENT '账号ID',
    `ref_date` Date,
    `user_source` UInt64,
    `int_page_read_user` UInt16,
    `int_page_read_count` UInt16,
    `ori_page_read_user` UInt16,
    `ori_page_read_count` UInt16,
    `share_user` UInt16,
    `share_count` UInt16,
    `add_to_fav_user` UInt16,
    `add_to_fav_count` UInt16
)
ENGINE = MergeTree
PARTITION BY toYYYYMMDD(ref_date)
ORDER BY ref_date
SETTINGS index_granularity = 8192
```

**ALTER TABLE 语句**：
```sql
-- 先重命名原表
ALTER TABLE wcai.user_read RENAME TO wcai.ods_user_read_detail;

-- 添加 account_id 字段
ALTER TABLE wcai.ods_user_read_detail 
ADD COLUMN IF NOT EXISTS account_id Nullable(Int64);
```

### 2.4 DWS 层表（汇总数据层）

#### 2.4.1 dws_users

**说明**: 用户汇总数据

| 字段名 | 类型 | 说明 |
|--------|------|------|
| account_id | Nullable(Int64) | 账号ID |
| ref_date | Date | 数据日期 |
| new_user | UInt32 | 新增用户 |
| cancel_user | UInt32 | 取消关注用户 |
| net_new_user | Int32 | 净增用户 |
| accumulated_user | Int32 | 累计用户 |

**建表语句**：
```sql
CREATE TABLE wcai.dws_users (
    `account_id` Nullable(Int64) COMMENT '账号ID',
    `ref_date` Date,
    `new_user` UInt32,
    `cancel_user` UInt32,
    `net_new_user` Int32,
    `accumulated_user` Int32
)
ENGINE = ReplacingMergeTree
PARTITION BY toYYYYMM(ref_date)
ORDER BY (ref_date, new_user, cancel_user, net_new_user, accumulated_user)
SETTINGS index_granularity = 8192
```

**ALTER TABLE 语句**：
```sql
ALTER TABLE wcai.dws_users 
ADD COLUMN IF NOT EXISTS account_id Nullable(Int64);
```

#### 2.4.2 dws_content_data

**说明**: 文章维度汇总

| 字段名 | 类型 | 说明 |
|--------|------|------|
| account_id | Nullable(Int64) | 账号ID |
| title | String | 标题 |
| create_time | Date | 创建时间 |
| latest_stat_date | Date | 最新统计日期 |
| msgid | String | 图文消息ID |
| author | String | 作者 |
| read_user_total | UInt32 | 阅读人数 |
| read_user_source_all | UInt32 | 全部来源阅读人数 |
| read_user_source_msg | UInt32 | 会话阅读人数 |
| read_user_source_chat | UInt32 | 好友转发阅读人数 |
| read_user_source_moments | UInt32 | 朋友圈阅读人数 |
| read_user_source_homepage | UInt32 | 公众号主页阅读人数 |
| read_user_source_other | UInt32 | 其他场景阅读人数 |
| read_user_source_recommend | UInt32 | 推荐阅读人数 |
| read_user_source_search | UInt32 | 搜索阅读人数 |
| share_user | UInt32 | 分享人数 |
| read_subscribe_user | UInt32 | 阅读关注人数 |
| url | String | 文章链接 |
| pull_time | DateTime | 数据拉取/入库时间 |

**建表语句**：
```sql
CREATE TABLE wcai.dws_content_data (
    `account_id` Nullable(Int64) COMMENT '账号ID',
    `title` String,
    `create_time` Date,
    `latest_stat_date` Date COMMENT '当前这条快照对应的最新统计日',
    `msgid` String,
    `author` String,
    `read_user_total` UInt32,
    `read_user_source_all` UInt32,
    `read_user_source_msg` UInt32,
    `read_user_source_chat` UInt32,
    `read_user_source_moments` UInt32,
    `read_user_source_homepage` UInt32,
    `read_user_source_other` UInt32,
    `read_user_source_recommend` UInt32,
    `read_user_source_search` UInt32,
    `share_user` UInt32,
    `read_subscribe_user` UInt32,
    `url` String,
    `pull_time` DateTime DEFAULT now() COMMENT '数据拉取/入库时间'
)
ENGINE = ReplacingMergeTree(pull_time)
PARTITION BY toYYYYMM(create_time)
ORDER BY (create_time, msgid)
SETTINGS index_granularity = 8192
```

**ALTER TABLE 语句**：
```sql
ALTER TABLE wcai.dws_content_data 
ADD COLUMN IF NOT EXISTS account_id Nullable(Int64);
```

#### 2.4.3 dws_bizsummary_channel_daily

**说明**: 渠道概况汇总

| 字段名 | 类型 | 说明 |
|--------|------|------|
| account_id | Nullable(Int64) | 账号ID |
| ref_date | Date | 日期 |
| read_user_cnt | UInt32 | 阅读人数 |
| share_user | UInt32 | 分享人数 |
| redirect_ori_page_count | UInt32 | 阅读原文次数 |
| redirect_ori_page_user | UInt32 | 阅读原文人数 |
| collection_count | UInt32 | 收藏次数 |
| collection_user | UInt32 | 收藏人数 |
| send_page_count | UInt32 | 群发篇数 |
| channel | String | 渠道 |

**建表语句**：
```sql
CREATE TABLE wcai.dws_bizsummary_channel_daily (
    `account_id` Nullable(Int64) COMMENT '账号ID',
    `ref_date` Date COMMENT '日期',
    `read_user_cnt` UInt32 COMMENT '阅读人数',
    `share_user` UInt32 COMMENT '分享人数',
    `redirect_ori_page_count` UInt32 COMMENT '阅读原文次数',
    `redirect_ori_page_user` UInt32 COMMENT '阅读原文人数',
    `collection_count` UInt32 COMMENT '收藏次数',
    `collection_user` UInt32 COMMENT '收藏人数',
    `send_page_count` UInt32 COMMENT '群发篇数',
    `channel` String COMMENT '渠道'
)
ENGINE = SummingMergeTree
PARTITION BY toYYYYMM(ref_date)
ORDER BY (ref_date, channel)
SETTINGS index_granularity = 8192
```

**ALTER TABLE 语句**：
```sql
ALTER TABLE wcai.dws_bizsummary_channel_daily 
ADD COLUMN IF NOT EXISTS account_id Nullable(Int64);
```

#### 2.4.4 dws_article_read

**说明**: 文章阅读汇总

| 字段名 | 类型 | 说明 |
|--------|------|------|
| account_id | Nullable(Int64) | 账号ID |
| create_time | Date | 创建时间 |
| msgid | String | 图文消息ID |
| title | String | 标题 |
| read_user_total | UInt32 | 阅读人数 |
| read_user_source_all | UInt32 | 全部来源阅读人数 |
| read_user_source_msg | UInt32 | 会话阅读人数 |
| read_user_source_chat | UInt32 | 好友转发阅读人数 |
| read_user_source_moments | UInt32 | 朋友圈阅读人数 |
| read_user_source_homepage | UInt32 | 公众号主页阅读人数 |
| read_user_source_other | UInt32 | 其他场景阅读人数 |
| read_user_source_recommend | UInt32 | 推荐阅读人数 |
| read_user_source_search | UInt32 | 搜索阅读人数 |
| share_user | UInt32 | 分享人数 |

**建表语句**：
```sql
CREATE TABLE wcai.dws_article_read (
    `account_id` Nullable(Int64) COMMENT '账号ID',
    `create_time` Date,
    `msgid` String,
    `title` String,
    `read_user_total` UInt32,
    `read_user_source_all` UInt32,
    `read_user_source_msg` UInt32,
    `read_user_source_chat` UInt32,
    `read_user_source_moments` UInt32,
    `read_user_source_homepage` UInt32,
    `read_user_source_other` UInt32,
    `read_user_source_recommend` UInt32,
    `read_user_source_search` UInt32,
    `share_user` UInt32
)
ENGINE = MergeTree
PARTITION BY toYYYYMMDD(create_time)
ORDER BY (msgid, create_time)
SETTINGS index_granularity = 8192
```

**ALTER TABLE 语句**：
```sql
ALTER TABLE wcai.dws_article_read 
ADD COLUMN IF NOT EXISTS account_id Nullable(Int64);
```

#### 2.4.5 dws_article_read_channel_daily

**说明**: 渠道文章阅读汇总

| 字段名 | 类型 | 说明 |
|--------|------|------|
| account_id | Nullable(Int64) | 账号ID |
| ref_date | Date | 数据日期 |
| channel | String | 渠道名称 |
| read_user_cnt | UInt64 | 阅读人数 |

**建表语句**：
```sql
CREATE TABLE wcai.dws_article_read_channel_daily (
    `account_id` Nullable(Int64) COMMENT '账号ID',
    `ref_date` Date COMMENT '数据日期',
    `channel` String COMMENT '渠道名称',
    `read_user_cnt` UInt64 COMMENT '阅读人数'
)
ENGINE = SummingMergeTree
PARTITION BY toYYYYMM(ref_date)
ORDER BY (ref_date, channel)
SETTINGS index_granularity = 8192
```

**ALTER TABLE 语句**：
```sql
ALTER TABLE wcai.dws_article_read_channel_daily 
ADD COLUMN IF NOT EXISTS account_id Nullable(Int64);
```

### 2.5 DIM 层表（维度数据层）

#### 2.5.1 dim_age_distribution

**说明**: 年龄分布维度（原 age_distribution）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| account_id | Nullable(Int64) | 账号ID |
| age | String | 年龄 |
| user_number | UInt32 | 用户数 |
| proportion | String | 占比 |
| pull_time | DateTime | 数据拉取/入库时间 |

**建表语句**：
```sql
CREATE TABLE wcai.dim_age_distribution (
    `account_id` Nullable(Int64) COMMENT '账号ID',
    `age` String COMMENT '年龄',
    `user_number` UInt32 COMMENT '用户数',
    `proportion` String COMMENT '占比',
    `pull_time` DateTime DEFAULT now() COMMENT '数据拉取/入库时间'
)
ENGINE = ReplacingMergeTree(pull_time)
ORDER BY age
SETTINGS index_granularity = 8192
```

**ALTER TABLE 语句**：
```sql
-- 先重命名原表
ALTER TABLE wcai.age_distribution RENAME TO wcai.dim_age_distribution;

-- 添加 account_id 字段
ALTER TABLE wcai.dim_age_distribution 
ADD COLUMN IF NOT EXISTS account_id Nullable(Int64);
```

#### 2.5.2 dim_sex_distribution

**说明**: 性别分布维度（原 sex_distribution）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| account_id | Nullable(Int64) | 账号ID |
| sex | String | 性别 |
| user_number | UInt32 | 用户数 |
| proportion | String | 占比 |
| pull_time | DateTime | 数据拉取/入库时间 |

**建表语句**：
```sql
CREATE TABLE wcai.dim_sex_distribution (
    `account_id` Nullable(Int64) COMMENT '账号ID',
    `sex` String COMMENT '性别',
    `user_number` UInt32 COMMENT '用户数',
    `proportion` String COMMENT '占比',
    `pull_time` DateTime DEFAULT now() COMMENT '数据拉取/入库时间'
)
ENGINE = ReplacingMergeTree(pull_time)
ORDER BY sex
SETTINGS index_granularity = 8192
```

**ALTER TABLE 语句**：
```sql
-- 先重命名原表
ALTER TABLE wcai.sex_distribution RENAME TO wcai.dim_sex_distribution;

-- 添加 account_id 字段
ALTER TABLE wcai.dim_sex_distribution 
ADD COLUMN IF NOT EXISTS account_id Nullable(Int64);
```

#### 2.5.3 dim_terrain_distribution

**说明**: 地域分布维度（原 terrain_distribution）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| account_id | Nullable(Int64) | 账号ID |
| terrain | String | 地域位置 |
| user_number | UInt32 | 用户数 |
| proportion | String | 占比 |
| pull_time | DateTime | 数据拉取/入库时间 |

**建表语句**：
```sql
CREATE TABLE wcai.dim_terrain_distribution (
    `account_id` Nullable(Int64) COMMENT '账号ID',
    `terrain` String COMMENT '地域位置',
    `user_number` UInt32 COMMENT '用户数',
    `proportion` String COMMENT '占比',
    `pull_time` DateTime DEFAULT now() COMMENT '数据拉取/入库时间'
)
ENGINE = ReplacingMergeTree(pull_time)
ORDER BY terrain
SETTINGS index_granularity = 8192
```

**ALTER TABLE 语句**：
```sql
-- 先重命名原表
ALTER TABLE wcai.terrain_distribution RENAME TO wcai.dim_terrain_distribution;

-- 添加 account_id 字段
ALTER TABLE wcai.dim_terrain_distribution 
ADD COLUMN IF NOT EXISTS account_id Nullable(Int64);
```

#### 2.5.4 dim_channel_distribution

**说明**: 渠道组成维度（原 channel_composition）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| account_id | Nullable(Int64) | 账号ID |
| channel | String | 渠道 |
| user_number | UInt32 | 用户数 |
| proportion | String | 占比 |
| pull_time | DateTime | 数据拉取/入库时间 |

**建表语句**：
```sql
CREATE TABLE wcai.dim_channel_distribution (
    `account_id` Nullable(Int64) COMMENT '账号ID',
    `channel` String COMMENT '渠道',
    `user_number` UInt32 COMMENT '用户数',
    `proportion` String COMMENT '占比',
    `pull_time` DateTime DEFAULT now() COMMENT '数据拉取/入库时间'
)
ENGINE = ReplacingMergeTree(pull_time)
ORDER BY channel
SETTINGS index_granularity = 8192
```

**ALTER TABLE 语句**：
```sql
-- 先重命名原表
ALTER TABLE wcai.channel_composition RENAME TO wcai.dim_channel_distribution;

-- 添加 account_id 字段
ALTER TABLE wcai.dim_channel_distribution 
ADD COLUMN IF NOT EXISTS account_id Nullable(Int64);
```

---

## 三、ClickHouse 表重命名清单

| 原表名 | 新表名 | 说明 |
|--------|--------|------|
| article_perday | ods_article_read_by_source | DWD 层 - 文章阅读来源明细 |
| user_read | ods_user_read_detail | DWD 层 - 用户阅读明细 |
| age_distribution | dim_age_distribution | DIM 层 - 年龄分布维度 |
| sex_distribution | dim_sex_distribution | DIM 层 - 性别分布维度 |
| terrain_distribution | dim_terrain_distribution | DIM 层 - 地域分布维度 |
| channel_composition | dim_channel_distribution | DIM 层 - 渠道组成维度 |

**重命名语句**：
```sql
-- DWD 层重命名
ALTER TABLE wcai.article_perday RENAME TO wcai.ods_article_read_by_source;
ALTER TABLE wcai.user_read RENAME TO wcai.ods_user_read_detail;

-- DIM 层重命名
ALTER TABLE wcai.age_distribution RENAME TO wcai.dim_age_distribution;
ALTER TABLE wcai.sex_distribution RENAME TO wcai.dim_sex_distribution;
ALTER TABLE wcai.terrain_distribution RENAME TO wcai.dim_terrain_distribution;
ALTER TABLE wcai.channel_composition RENAME TO wcai.dim_channel_distribution;
```
