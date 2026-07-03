# SQL Scripts

## 20260610_content_phase1_database.sql

SpaceMV问道内容创作发布第一阶段数据库脚本，目标库：

```text
YOUR_MYSQL_HOST:3306/ry-cloud2
```

脚本内容：

- 校验当前库必须是 `ry-cloud2`
- 校验旧微信公众号文章表 `txwx_article` 必须存在
- 创建 `content_article`
- 创建 `content_publish_job`
- 创建 `content_publish_result`
- 插入一条固定 slug 的 smoke-test 文章：`phase1-smoke-20260610`
- 插入一条 `SITE_WENDAO` 发布任务测试数据
- 插入一条发布结果测试数据
- 校验计划索引和 `slug` 唯一约束

执行示例：

```bash
mysql -hYOUR_MYSQL_HOST -P3306 -uYOUR_DB_USER -p ry-cloud2 < 20260610_content_phase1_database.sql
```

执行成功后，最后会输出 smoke-test 文章、发布任务、发布结果，以及三张表的索引清单。

本脚本可重复执行；固定 smoke-test 数据会按 `slug = 'phase1-smoke-20260610'` 更新，不会重复插入文章主数据。

如果需要清理 smoke-test 数据，脚本末尾保留了三条注释掉的 `DELETE` 语句，只会清理该固定 slug 对应的数据。
