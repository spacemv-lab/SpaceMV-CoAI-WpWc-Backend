create database wcai;

use wcai;

CREATE TABLE ods_article
(
    msgid String,
    title String,
    create_time Date
)
    ENGINE = MergeTree()
PARTITION BY toYYYYMMDD(create_time)
ORDER BY create_time;

CREATE TABLE ods_article_read_daily
(
    ref_date Date,                    -- 统计日期
    msgid String,                     -- 消息ID (msgid_index格式)
    read_user_total UInt32,           -- 阅读人数（全部来源）
    read_user_source_all UInt32,      -- 来源：全部
    read_user_source_msg UInt32,      -- 来源：公众号消息
    read_user_source_chat UInt32,     -- 来源：聊天会话
    read_user_source_moments UInt32, -- 来源：朋友圈
    read_user_source_homepage UInt32, -- 来源：公众号主页
    read_user_source_other UInt32,    -- 来源：其他
    read_user_source_recommend UInt32, -- 来源：推荐
    read_user_source_search UInt32    -- 来源：搜一搜
)
    ENGINE = MergeTree()
PARTITION BY toYYYYMMDD(ref_date)
ORDER BY (ref_date, msgid);

CREATE TABLE ods_article_share_daily
(
    ref_date Date,                    -- 统计日期
    msgid String,                     -- 消息ID (msgid_index格式)
    share_user UInt32                 -- 分享人数
)
    ENGINE = MergeTree()
PARTITION BY toYYYYMMDD(ref_date)
ORDER BY (ref_date, msgid);


CREATE TABLE ods_article_summary_daily
(
    ref_date Date,                      -- 统计日期
    read_user_total UInt32,             -- 阅读人数（全部来源）
    read_user_source_all UInt32,        -- 阅读来源：全部
    read_user_source_msg UInt32,        -- 阅读来源：公众号消息
    read_user_source_chat UInt32,       -- 阅读来源：聊天会话
    read_user_source_moments UInt32,    -- 阅读来源：朋友圈
    read_user_source_homepage UInt32,   -- 阅读来源：公众号主页
    read_user_source_other UInt32,      -- 阅读来源：其他
    read_user_source_recommend UInt32,  -- 阅读来源：推荐
    read_user_source_search UInt32,     -- 阅读来源：搜一搜
    share_user UInt32,                 -- 分享人数
    zaikan_user UInt32,                -- 爱心赞人数
    like_user UInt32,                  -- 拇指赞人数
    comment_count UInt32,              -- 留言条数
    collection_user UInt32,            -- 微信收藏人数
    redirect_ori_page_user UInt32,     -- 跳转原文人数
    send_page_count UInt32            -- 发布篇数
)
    ENGINE = MergeTree()
PARTITION BY toYYYYMMDD(ref_date)
ORDER BY ref_date;

CREATE TABLE ods_users
(
    ref_date Date,
    user_source UInt16,
    new_user UInt16,
    cancel_user UInt16
)
    ENGINE = MergeTree()
PARTITION BY toYYYYMMDD(ref_date)
ORDER BY ref_date;


CREATE TABLE dws_users
(
    ref_date Date,
    new_user UInt16,
    cancel_user UInt16,
    net_new_user UInt16,
    accumulated_user UInt16
)
    ENGINE = MergeTree()
PARTITION BY toYYYYMMDD(ref_date)
ORDER BY ref_date;

CREATE TABLE dws_article_read
(
    create_time Date,                  -- 文章发表日期
    msgid String,                      -- 消息ID (msgid_index格式)
    title String,                      -- 文章标题
    read_user_total UInt32,            -- 阅读人数（全部来源）
    read_user_source_all UInt32,       -- 来源：全部
    read_user_source_msg UInt32,       -- 来源：公众号消息
    read_user_source_chat UInt32,      -- 来源：聊天会话
    read_user_source_moments UInt32,   -- 来源：朋友圈
    read_user_source_homepage UInt32,  -- 来源：公众号主页
    read_user_source_other UInt32,     -- 来源：其他
    read_user_source_recommend UInt32, -- 来源：推荐
    read_user_source_search UInt32,     -- 来源：搜一搜
    share_user UInt32                   -- 分享人数
)
    ENGINE = MergeTree()
PARTITION BY toYYYYMMDD(create_time)
ORDER BY (msgid, create_time);

CREATE TABLE article_perday
(
    ref_date Date,
    user_source UInt16,
    msgid String,
    title String,
    int_page_read_user UInt16,
    int_page_read_count UInt16,
    ori_page_read_user UInt16,
    ori_page_read_count UInt16,
    share_user UInt16,
    share_count UInt16,
    add_to_fav_user UInt16,
    add_to_fav_count UInt16
)
    ENGINE = MergeTree()
PARTITION BY toYYYYMMDD(ref_date)
ORDER BY ref_date;

CREATE TABLE user_read
(
    ref_date Date,
    user_source UInt64,
    int_page_read_user UInt16,
    int_page_read_count UInt16,
    ori_page_read_user UInt16,
    ori_page_read_count UInt16,
    share_user UInt16,
    share_count UInt16,
    add_to_fav_user UInt16,
    add_to_fav_count UInt16
)
    ENGINE = MergeTree()
PARTITION BY toYYYYMMDD(ref_date)
ORDER BY ref_date;