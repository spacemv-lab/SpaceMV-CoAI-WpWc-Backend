-- MySQL dump 10.13  Distrib 9.6.0, for macos14.8 (x86_64)
--
-- Host: ***REMOVED***    Database: wpai-prod
-- ------------------------------------------------------
-- Server version	5.7.30-0ubuntu0.18.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `QRTZ_BLOB_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_BLOB_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_BLOB_TRIGGERS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `blob_data` blob COMMENT '存放持久化Trigger对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`) USING BTREE,
  CONSTRAINT `QRTZ_BLOB_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `QRTZ_TRIGGERS` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='Blob类型的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_CALENDARS`
--

DROP TABLE IF EXISTS `QRTZ_CALENDARS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_CALENDARS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `calendar_name` varchar(200) NOT NULL COMMENT '日历名称',
  `calendar` blob NOT NULL COMMENT '存放持久化calendar对象',
  PRIMARY KEY (`sched_name`,`calendar_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='日历信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_CRON_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_CRON_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_CRON_TRIGGERS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `cron_expression` varchar(200) NOT NULL COMMENT 'cron表达式',
  `time_zone_id` varchar(80) DEFAULT NULL COMMENT '时区',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`) USING BTREE,
  CONSTRAINT `QRTZ_CRON_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `QRTZ_TRIGGERS` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='Cron类型的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_FIRED_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_FIRED_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_FIRED_TRIGGERS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `entry_id` varchar(95) NOT NULL COMMENT '调度器实例id',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `instance_name` varchar(200) NOT NULL COMMENT '调度器实例名',
  `fired_time` bigint(13) NOT NULL COMMENT '触发的时间',
  `sched_time` bigint(13) NOT NULL COMMENT '定时器制定的时间',
  `priority` int(11) NOT NULL COMMENT '优先级',
  `state` varchar(16) NOT NULL COMMENT '状态',
  `job_name` varchar(200) DEFAULT NULL COMMENT '任务名称',
  `job_group` varchar(200) DEFAULT NULL COMMENT '任务组名',
  `is_nonconcurrent` varchar(1) DEFAULT NULL COMMENT '是否并发',
  `requests_recovery` varchar(1) DEFAULT NULL COMMENT '是否接受恢复执行',
  PRIMARY KEY (`sched_name`,`entry_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='已触发的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_JOB_DETAILS`
--

DROP TABLE IF EXISTS `QRTZ_JOB_DETAILS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_JOB_DETAILS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `job_name` varchar(200) NOT NULL COMMENT '任务名称',
  `job_group` varchar(200) NOT NULL COMMENT '任务组名',
  `description` varchar(250) DEFAULT NULL COMMENT '相关介绍',
  `job_class_name` varchar(250) NOT NULL COMMENT '执行任务类名称',
  `is_durable` varchar(1) NOT NULL COMMENT '是否持久化',
  `is_nonconcurrent` varchar(1) NOT NULL COMMENT '是否并发',
  `is_update_data` varchar(1) NOT NULL COMMENT '是否更新数据',
  `requests_recovery` varchar(1) NOT NULL COMMENT '是否接受恢复执行',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`job_name`,`job_group`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='任务详细信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_LOCKS`
--

DROP TABLE IF EXISTS `QRTZ_LOCKS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_LOCKS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `lock_name` varchar(40) NOT NULL COMMENT '悲观锁名称',
  PRIMARY KEY (`sched_name`,`lock_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='存储的悲观锁信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_PAUSED_TRIGGER_GRPS`
--

DROP TABLE IF EXISTS `QRTZ_PAUSED_TRIGGER_GRPS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_PAUSED_TRIGGER_GRPS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  PRIMARY KEY (`sched_name`,`trigger_group`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='暂停的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_SCHEDULER_STATE`
--

DROP TABLE IF EXISTS `QRTZ_SCHEDULER_STATE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_SCHEDULER_STATE` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `instance_name` varchar(200) NOT NULL COMMENT '实例名称',
  `last_checkin_time` bigint(13) NOT NULL COMMENT '上次检查时间',
  `checkin_interval` bigint(13) NOT NULL COMMENT '检查间隔时间',
  PRIMARY KEY (`sched_name`,`instance_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='调度器状态表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_SIMPLE_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_SIMPLE_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_SIMPLE_TRIGGERS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `repeat_count` bigint(7) NOT NULL COMMENT '重复的次数统计',
  `repeat_interval` bigint(12) NOT NULL COMMENT '重复的间隔时间',
  `times_triggered` bigint(10) NOT NULL COMMENT '已经触发的次数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`) USING BTREE,
  CONSTRAINT `QRTZ_SIMPLE_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `QRTZ_TRIGGERS` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='简单触发器的信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_SIMPROP_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_SIMPROP_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_SIMPROP_TRIGGERS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `str_prop_1` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第一个参数',
  `str_prop_2` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第二个参数',
  `str_prop_3` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第三个参数',
  `int_prop_1` int(11) DEFAULT NULL COMMENT 'int类型的trigger的第一个参数',
  `int_prop_2` int(11) DEFAULT NULL COMMENT 'int类型的trigger的第二个参数',
  `long_prop_1` bigint(20) DEFAULT NULL COMMENT 'long类型的trigger的第一个参数',
  `long_prop_2` bigint(20) DEFAULT NULL COMMENT 'long类型的trigger的第二个参数',
  `dec_prop_1` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第一个参数',
  `dec_prop_2` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第二个参数',
  `bool_prop_1` varchar(1) DEFAULT NULL COMMENT 'Boolean类型的trigger的第一个参数',
  `bool_prop_2` varchar(1) DEFAULT NULL COMMENT 'Boolean类型的trigger的第二个参数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`) USING BTREE,
  CONSTRAINT `QRTZ_SIMPROP_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `QRTZ_TRIGGERS` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='同步机制的行锁表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_TRIGGERS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT '触发器的名字',
  `trigger_group` varchar(200) NOT NULL COMMENT '触发器所属组的名字',
  `job_name` varchar(200) NOT NULL COMMENT 'qrtz_job_details表job_name的外键',
  `job_group` varchar(200) NOT NULL COMMENT 'qrtz_job_details表job_group的外键',
  `description` varchar(250) DEFAULT NULL COMMENT '相关介绍',
  `next_fire_time` bigint(13) DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
  `prev_fire_time` bigint(13) DEFAULT NULL COMMENT '下一次触发时间（默认为-1表示不触发）',
  `priority` int(11) DEFAULT NULL COMMENT '优先级',
  `trigger_state` varchar(16) NOT NULL COMMENT '触发器状态',
  `trigger_type` varchar(8) NOT NULL COMMENT '触发器的类型',
  `start_time` bigint(13) NOT NULL COMMENT '开始时间',
  `end_time` bigint(13) DEFAULT NULL COMMENT '结束时间',
  `calendar_name` varchar(200) DEFAULT NULL COMMENT '日程表名称',
  `misfire_instr` smallint(2) DEFAULT NULL COMMENT '补偿执行的策略',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`) USING BTREE,
  KEY `sched_name` (`sched_name`,`job_name`,`job_group`) USING BTREE,
  CONSTRAINT `QRTZ_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `QRTZ_JOB_DETAILS` (`sched_name`, `job_name`, `job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='触发器详细信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gen_table`
--

DROP TABLE IF EXISTS `gen_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gen_table` (
  `table_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `tpl_web_type` varchar(30) DEFAULT '' COMMENT '前端模板类型（element-ui模版 element-plus模版）',
  `package_name` varchar(100) DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) DEFAULT NULL COMMENT '生成功能作者',
  `gen_type` char(1) DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='代码生成业务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gen_table_column`
--

DROP TABLE IF EXISTS `gen_table_column`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gen_table_column` (
  `column_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` bigint(20) DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) DEFAULT '' COMMENT '字典类型',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='代码生成业务表字段';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `product_basic_info`
--

DROP TABLE IF EXISTS `product_basic_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_basic_info` (
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
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_product_type` (`product_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='产品基础信息正式表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `product_basic_info_temp`
--

DROP TABLE IF EXISTS `product_basic_info_temp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_basic_info_temp` (
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
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_product_type` (`product_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='产品基础信息临时表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `product_cert`
--

DROP TABLE IF EXISTS `product_cert`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_cert` (
  `cert_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `cert_name` varchar(200) NOT NULL COMMENT '产品认证名称',
  `image_url` varchar(500) NOT NULL COMMENT '图片地址',
  `sort_order` int(4) DEFAULT '0' COMMENT '排序序号',
  `jump_url` varchar(500) DEFAULT NULL COMMENT '跳转链接',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`cert_id`) USING BTREE,
  KEY `idx_sort_order` (`sort_order`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='产品认证正式表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `product_cert_temp`
--

DROP TABLE IF EXISTS `product_cert_temp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_cert_temp` (
  `cert_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `cert_name` varchar(200) NOT NULL COMMENT '产品认证名称',
  `image_url` varchar(500) NOT NULL COMMENT '图片地址',
  `sort_order` int(4) DEFAULT '0' COMMENT '排序序号',
  `jump_url` varchar(500) DEFAULT NULL COMMENT '跳转链接',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`cert_id`) USING BTREE,
  KEY `idx_sort_order` (`sort_order`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='产品认证临时表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_config`
--

DROP TABLE IF EXISTS `sys_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_config` (
  `config_id` int(5) NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='参数配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_dept`
--

DROP TABLE IF EXISTS `sys_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dept` (
  `dept_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父部门id',
  `ancestors` varchar(50) DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) DEFAULT '' COMMENT '部门名称',
  `order_num` int(4) DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(20) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `status` char(1) DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=202 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='部门表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_dict_data`
--

DROP TABLE IF EXISTS `sys_dict_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_data` (
  `dict_code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int(4) DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='字典数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_dict_type`
--

DROP TABLE IF EXISTS `sys_dict_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_type` (
  `dict_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`) USING BTREE,
  UNIQUE KEY `dict_type` (`dict_type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='字典类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_job`
--

DROP TABLE IF EXISTS `sys_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_job` (
  `job_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`,`job_name`,`job_group`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='定时任务调度表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_job_log`
--

DROP TABLE IF EXISTS `sys_job_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_job_log` (
  `job_log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) DEFAULT NULL COMMENT '日志信息',
  `status` char(1) DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) DEFAULT '' COMMENT '异常信息',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='定时任务调度日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_logininfor`
--

DROP TABLE IF EXISTS `sys_logininfor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_logininfor` (
  `info_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) DEFAULT '' COMMENT '登录IP地址',
  `status` char(1) DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) DEFAULT '' COMMENT '提示信息',
  `access_time` datetime DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`) USING BTREE,
  KEY `idx_sys_logininfor_s` (`status`) USING BTREE,
  KEY `idx_sys_logininfor_lt` (`access_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=685 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='系统访问记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_menu` (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父菜单ID',
  `order_num` int(4) DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(200) DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) DEFAULT '' COMMENT '路由名称',
  `is_frame` int(1) DEFAULT '1' COMMENT '是否为外链（0是 1否）',
  `is_cache` int(1) DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2043 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='菜单权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_notice`
--

DROP TABLE IF EXISTS `sys_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_notice` (
  `notice_id` int(4) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) NOT NULL COMMENT '公告标题',
  `notice_type` char(1) NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob COMMENT '公告内容',
  `status` char(1) DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='通知公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_oper_log`
--

DROP TABLE IF EXISTS `sys_oper_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_oper_log` (
  `oper_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) DEFAULT '' COMMENT '模块标题',
  `business_type` int(2) DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(200) DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) DEFAULT '' COMMENT '请求方式',
  `operator_type` int(1) DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) DEFAULT '' COMMENT '返回参数',
  `status` int(1) DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint(20) DEFAULT '0' COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`) USING BTREE,
  KEY `idx_sys_oper_log_bt` (`business_type`) USING BTREE,
  KEY `idx_sys_oper_log_s` (`status`) USING BTREE,
  KEY `idx_sys_oper_log_ot` (`oper_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1234 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='操作日志记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_post`
--

DROP TABLE IF EXISTS `sys_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_post` (
  `post_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) NOT NULL COMMENT '岗位名称',
  `post_sort` int(4) NOT NULL COMMENT '显示顺序',
  `status` char(1) NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='岗位信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) NOT NULL COMMENT '角色权限字符串',
  `role_sort` int(4) NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) DEFAULT '1' COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) DEFAULT '1' COMMENT '部门树选择项是否关联显示',
  `status` char(1) NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=116 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='角色信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_role_dept`
--

DROP TABLE IF EXISTS `sys_role_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_dept` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`,`dept_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='角色和部门关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_role_menu`
--

DROP TABLE IF EXISTS `sys_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='角色和菜单关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) DEFAULT '' COMMENT '手机号码',
  `sex` char(1) DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) DEFAULT '' COMMENT '密码',
  `status` char(1) DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `pwd_update_date` datetime DEFAULT NULL COMMENT '密码最后更新时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=119 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_user_post`
--

DROP TABLE IF EXISTS `sys_user_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_post` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `post_id` bigint(20) NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`,`post_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户与岗位关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户和角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_account`
--

DROP TABLE IF EXISTS `txwx_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `txwx_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '账号ID',
  `channel_id` bigint(20) NOT NULL COMMENT '自媒体渠道ID',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `account_name` varchar(100) NOT NULL COMMENT '账号名称',
  `account_no` varchar(100) DEFAULT NULL COMMENT '账号编号',
  `appid` varchar(255) DEFAULT NULL COMMENT '三方账号appid',
  `secret` varchar(255) DEFAULT NULL COMMENT '三方账号secret',
  `token` varchar(255) DEFAULT NULL COMMENT '三方账号token',
  `encoding_aes_key` varchar(255) DEFAULT NULL COMMENT '加密key',
  `status` char(1) DEFAULT '1' COMMENT '状态：0-停用 1-启用',
  `last_sync_time` datetime DEFAULT NULL COMMENT '最近一次同步时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` tinyint(3) unsigned zerofill DEFAULT '000',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_channel_id_app_ida_del` (`channel_id`,`appid`,`del_flag`),
  KEY `idx_channel_id` (`channel_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_product_id_channle_id` (`product_id`,`channel_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COMMENT='三方账号表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_application_scenario`
--

DROP TABLE IF EXISTS `txwx_application_scenario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`scenario_id`) USING BTREE,
  KEY `idx_product_type` (`product_type`) USING BTREE,
  KEY `idx_sort_order` (`sort_order`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='应用场景正式表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_application_scenario_temp`
--

DROP TABLE IF EXISTS `txwx_application_scenario_temp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`scenario_id`) USING BTREE,
  KEY `idx_product_type` (`product_type`) USING BTREE,
  KEY `idx_sort_order` (`sort_order`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='应用场景临时表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_article`
--

DROP TABLE IF EXISTS `txwx_article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `txwx_article` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `media_id` varchar(128) DEFAULT NULL COMMENT '微信返回的media_id',
  `article_id` varchar(128) DEFAULT NULL COMMENT '微信返回的article_id(已发布文章)',
  `title` varchar(64) NOT NULL DEFAULT '' COMMENT '标题',
  `author` varchar(16) DEFAULT NULL COMMENT '作者',
  `digest` varchar(128) DEFAULT NULL COMMENT '摘要',
  `content` mediumtext COMMENT '内容',
  `thumb_media_id` varchar(128) DEFAULT NULL COMMENT '封面图片素材id',
  `thumb_url` varchar(500) DEFAULT NULL COMMENT '封面图片素材url',
  `need_open_comment` tinyint(1) DEFAULT '0' COMMENT '是否打开评论，0不打开，1打开',
  `only_fans_can_comment` tinyint(1) DEFAULT '0' COMMENT '是否粉丝才可评论，0所有人可评论，1粉丝才可评论',
  `article_type` varchar(16) DEFAULT 'news' COMMENT '文章类型',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态：0待提交 1待审核 2审核不通过 3审核通过待发布 4已发布',
  `submitter` varchar(64) DEFAULT NULL COMMENT '提交人',
  `submitter_id` bigint(20) DEFAULT NULL COMMENT '提交人的id',
  `reviewer` varchar(64) DEFAULT NULL COMMENT '审核人',
  `publisher` varchar(64) DEFAULT NULL COMMENT '发布人',
  `publish_id` varchar(128) DEFAULT NULL COMMENT '发布任务的id',
  `msg_data_id` varchar(128) DEFAULT NULL COMMENT '消息的数据ID',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0代表存在 1代表删除)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `account_id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_media_id` (`media_id`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_submitter` (`submitter`) USING BTREE,
  KEY `idx_reviewer` (`reviewer`) USING BTREE,
  KEY `idx_account_id` (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='微信公众号文章表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_carousel_image`
--

DROP TABLE IF EXISTS `txwx_carousel_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`carousel_id`) USING BTREE,
  KEY `idx_is_show` (`is_show`) USING BTREE,
  KEY `idx_sort_order` (`sort_order`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='首页轮播图配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_carousel_image_temp`
--

DROP TABLE IF EXISTS `txwx_carousel_image_temp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`carousel_id`) USING BTREE,
  KEY `idx_is_show` (`is_show`) USING BTREE,
  KEY `idx_sort_order` (`sort_order`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='首页轮播图配置临时表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_channel`
--

DROP TABLE IF EXISTS `txwx_channel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `txwx_channel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '渠道ID',
  `channel_name` varchar(100) NOT NULL COMMENT '渠道名称',
  `channel_type` varchar(32) NOT NULL COMMENT '渠道类型：如：wechat/douyin/xiaohongshu',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` tinyint(3) unsigned zerofill NOT NULL DEFAULT '000',
  `create_time` datetime DEFAULT NULL,
  `channel_desc` varchar(255) DEFAULT NULL COMMENT '渠道描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='三方渠道列表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_company_info`
--

DROP TABLE IF EXISTS `txwx_company_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `txwx_company_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `company_name` varchar(200) NOT NULL COMMENT '公司名称',
  `copyright_info` varchar(500) DEFAULT NULL COMMENT '版权信息',
  `version_number` varchar(50) DEFAULT NULL COMMENT '版本号',
  `company_address` varchar(500) DEFAULT NULL COMMENT '公司地址',
  `security_record` varchar(100) DEFAULT NULL COMMENT '网安备案信息',
  `icp_record` varchar(100) DEFAULT NULL COMMENT 'ICP备案信息',
  `business_cooperation` varchar(500) DEFAULT NULL COMMENT '商务合作途径',
  `resume_delivery` varchar(500) DEFAULT NULL COMMENT '简历投递途径',
  `logo_url` varchar(500) DEFAULT NULL COMMENT '公司Logo URL',
  `addr_url` varchar(500) DEFAULT NULL COMMENT '公司地址图片 URL',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='公司基本信息正式表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_company_info_temp`
--

DROP TABLE IF EXISTS `txwx_company_info_temp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `txwx_company_info_temp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `company_name` varchar(200) NOT NULL COMMENT '公司名称',
  `copyright_info` varchar(500) DEFAULT NULL COMMENT '版权信息',
  `version_number` varchar(50) DEFAULT NULL COMMENT '版本号',
  `company_address` varchar(500) DEFAULT NULL COMMENT '公司地址',
  `security_record` varchar(100) DEFAULT NULL COMMENT '网安备案信息',
  `icp_record` varchar(100) DEFAULT NULL COMMENT 'ICP备案信息',
  `business_cooperation` varchar(500) DEFAULT NULL COMMENT '商务合作途径',
  `resume_delivery` varchar(500) DEFAULT NULL COMMENT '简历投递途径',
  `logo_url` varchar(500) DEFAULT NULL COMMENT '公司Logo URL',
  `addr_url` varchar(500) DEFAULT NULL COMMENT '公司地址图片 URL',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='公司基本信息临时表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_company_profile`
--

DROP TABLE IF EXISTS `txwx_company_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `txwx_company_profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `profile_one` text COMMENT '公司简介一（富文本）',
  `profile_two` text COMMENT '公司简介二（富文本）',
  `profile_three` text COMMENT '公司简介三（富文本）',
  `profile_four` text COMMENT '公司简介四（富文本）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='公司简介正式表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_company_profile_temp`
--

DROP TABLE IF EXISTS `txwx_company_profile_temp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `txwx_company_profile_temp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `profile_one` text COMMENT '公司简介一（富文本）',
  `profile_two` text COMMENT '公司简介二（富文本）',
  `profile_three` text COMMENT '公司简介三（富文本）',
  `profile_four` text COMMENT '公司简介四（富文本）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='公司简介临时表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_drop_button`
--

DROP TABLE IF EXISTS `txwx_drop_button`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `txwx_drop_button` (
  `drop_button_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '下拉按钮主键',
  `button_id` bigint(20) NOT NULL COMMENT '按钮主键（关联txwx_page_button）',
  `text` varchar(200) DEFAULT NULL COMMENT '按钮文案',
  `link` varchar(500) DEFAULT NULL COMMENT '跳转地址',
  `state` varchar(200) DEFAULT NULL COMMENT '形态 1-complete 新窗口打开链接；2-completeSelf 当前窗口打开链接；3-completeNav nav路由；4-developing 正在开发中',
  `sort_order` int(4) DEFAULT '0' COMMENT '排序序号',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`drop_button_id`) USING BTREE,
  KEY `idx_button_id` (`button_id`) USING BTREE,
  KEY `idx_sort_order` (`sort_order`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='下拉按钮配置正式表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_drop_button_temp`
--

DROP TABLE IF EXISTS `txwx_drop_button_temp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `txwx_drop_button_temp` (
  `drop_button_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '下拉按钮主键',
  `button_id` bigint(20) NOT NULL COMMENT '按钮主键（关联txwx_page_button_temp）',
  `text` varchar(200) DEFAULT NULL COMMENT '按钮文案',
  `link` varchar(500) DEFAULT NULL COMMENT '跳转地址',
  `state` varchar(200) DEFAULT NULL COMMENT '形态 1-complete 新窗口打开链接；2-completeSelf 当前窗口打开链接；3-completeNav nav路由；4-developing 正在开发中',
  `sort_order` int(4) DEFAULT '0' COMMENT '排序序号',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`drop_button_id`) USING BTREE,
  KEY `idx_button_id` (`button_id`) USING BTREE,
  KEY `idx_sort_order` (`sort_order`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='下拉按钮配置临时表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_focus`
--

DROP TABLE IF EXISTS `txwx_focus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `txwx_focus` (
  `focus_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `focus_name` varchar(100) NOT NULL COMMENT '名称',
  `image_url` varchar(500) NOT NULL COMMENT '图片地址',
  `sort_order` int(4) DEFAULT '0' COMMENT '排序序号',
  `jump_url` varchar(500) DEFAULT NULL COMMENT '跳转链接',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`focus_id`) USING BTREE,
  KEY `idx_sort_order` (`sort_order`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='关注配置正式表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_focus_temp`
--

DROP TABLE IF EXISTS `txwx_focus_temp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `txwx_focus_temp` (
  `focus_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `focus_name` varchar(100) NOT NULL COMMENT '名称',
  `image_url` varchar(500) NOT NULL COMMENT '图片地址',
  `sort_order` int(4) DEFAULT '0' COMMENT '排序序号',
  `jump_url` varchar(500) DEFAULT NULL COMMENT '跳转链接',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`focus_id`) USING BTREE,
  KEY `idx_sort_order` (`sort_order`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='关注配置临时表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_graphic_information_image`
--

DROP TABLE IF EXISTS `txwx_graphic_information_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `account_id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_media_id` (`media_id`) USING BTREE,
  KEY `idx_account_id` (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='微信公众号图文消息图片表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_main_product`
--

DROP TABLE IF EXISTS `txwx_main_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`product_id`) USING BTREE,
  KEY `idx_product_name` (`product_name`) USING BTREE,
  KEY `idx_is_show` (`is_show`) USING BTREE,
  KEY `idx_sort_order` (`sort_order`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='首页主要产品配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_main_product_temp`
--

DROP TABLE IF EXISTS `txwx_main_product_temp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `txwx_main_product_temp` (
  `product_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主打产品主键',
  `product_name` varchar(200) NOT NULL COMMENT '产品名称',
  `image_url` varchar(500) NOT NULL COMMENT '产品图片地址',
  `description` varchar(1000) DEFAULT NULL COMMENT '产品描述',
  `jump_url` varchar(500) DEFAULT NULL COMMENT '跳转链接',
  `is_show` char(1) DEFAULT NULL COMMENT '是否显示（0-否 1-是）',
  `sort_order` int(4) DEFAULT '0' COMMENT '排序序号',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`product_id`) USING BTREE,
  KEY `idx_is_show` (`is_show`) USING BTREE,
  KEY `idx_sort_order` (`sort_order`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='首页主打产品配置临时表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_page_button`
--

DROP TABLE IF EXISTS `txwx_page_button`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `txwx_page_button` (
  `button_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '按钮主键',
  `page_code` varchar(100) NOT NULL COMMENT '页面标识',
  `button_type` char(1) NOT NULL COMMENT '按钮类型（1-文字按钮 2-图片按钮）',
  `button_text` varchar(200) DEFAULT NULL COMMENT '按钮文案',
  `background_color` varchar(20) DEFAULT NULL COMMENT '背景色',
  `jump_url` varchar(500) DEFAULT NULL COMMENT '跳转地址',
  `image_url` varchar(500) DEFAULT NULL COMMENT '图片URL',
  `is_show` char(1) DEFAULT '1' COMMENT '是否对外展示（0-否 1-是）',
  `is_publish` char(1) DEFAULT NULL COMMENT '是否发布',
  `state` char(1) DEFAULT '1' COMMENT '形态 0-nav路由 1-新窗口打开链接 2-当前窗口打开链接 3-正在开发中',
  `sort_order` int(4) DEFAULT '0' COMMENT '排序序号',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `saved_by` varchar(64) DEFAULT NULL COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT NULL COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  PRIMARY KEY (`button_id`) USING BTREE,
  KEY `idx_page_code` (`page_code`) USING BTREE,
  KEY `idx_button_type` (`button_type`) USING BTREE,
  KEY `idx_is_show` (`is_show`) USING BTREE,
  KEY `idx_sort_order` (`sort_order`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=164 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='页面按钮配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_page_button_temp`
--

DROP TABLE IF EXISTS `txwx_page_button_temp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `txwx_page_button_temp` (
  `button_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '按钮主键',
  `page_code` varchar(100) NOT NULL COMMENT '页面标识',
  `button_type` char(1) NOT NULL COMMENT '按钮类型（1-文字按钮 2-图片按钮）',
  `button_text` varchar(200) DEFAULT NULL COMMENT '按钮文案',
  `background_color` varchar(20) DEFAULT NULL COMMENT '背景色',
  `jump_url` varchar(500) DEFAULT NULL COMMENT '跳转地址',
  `image_url` varchar(500) DEFAULT NULL COMMENT '图片URL',
  `is_show` char(1) DEFAULT '1' COMMENT '是否对外展示（0-否 1-是）',
  `is_publish` char(1) DEFAULT NULL COMMENT '是否发布',
  `state` char(1) DEFAULT '1' COMMENT '形态 0-nav路由 1-新窗口打开链接 2-当前窗口打开链接 3-正在开发中',
  `sort_order` int(4) DEFAULT '0' COMMENT '排序序号',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `saved_by` varchar(64) DEFAULT NULL COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT NULL COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  PRIMARY KEY (`button_id`) USING BTREE,
  KEY `idx_page_code` (`page_code`) USING BTREE,
  KEY `idx_button_type` (`button_type`) USING BTREE,
  KEY `idx_is_show` (`is_show`) USING BTREE,
  KEY `idx_sort_order` (`sort_order`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=268 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='页面按钮配置临时表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_permanent_material_image`
--

DROP TABLE IF EXISTS `txwx_permanent_material_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `txwx_permanent_material_image` (
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
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `account_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_media_id` (`media_id`),
  KEY `idx_account_id` (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COMMENT='微信公众号永久素材表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_product`
--

DROP TABLE IF EXISTS `txwx_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `txwx_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '产品ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `product_name` varchar(100) NOT NULL COMMENT '产品名称',
  `product_code` varchar(50) DEFAULT '' COMMENT '产品编号',
  `product_desc` varchar(500) DEFAULT NULL COMMENT '产品描述',
  `status` char(1) DEFAULT '1' COMMENT '产品状态：0-停用 1-启用',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` tinyint(3) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COMMENT='自媒体产品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_product_basic_info`
--

DROP TABLE IF EXISTS `txwx_product_basic_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_product_type` (`product_type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='产品基础信息正式表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_product_basic_info_temp`
--

DROP TABLE IF EXISTS `txwx_product_basic_info_temp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_product_type` (`product_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='产品基础信息临时表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_product_cert`
--

DROP TABLE IF EXISTS `txwx_product_cert`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `txwx_product_cert` (
  `cert_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `cert_name` varchar(200) NOT NULL COMMENT '产品认证名称',
  `image_url` varchar(500) NOT NULL COMMENT '图片地址',
  `sort_order` int(4) DEFAULT '0' COMMENT '排序序号',
  `jump_url` varchar(500) DEFAULT NULL COMMENT '跳转链接',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`cert_id`) USING BTREE,
  KEY `idx_sort_order` (`sort_order`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='产品认证正式表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_product_cert_temp`
--

DROP TABLE IF EXISTS `txwx_product_cert_temp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `txwx_product_cert_temp` (
  `cert_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `cert_name` varchar(200) NOT NULL COMMENT '产品认证名称',
  `image_url` varchar(500) NOT NULL COMMENT '图片地址',
  `sort_order` int(4) DEFAULT '0' COMMENT '排序序号',
  `jump_url` varchar(500) DEFAULT NULL COMMENT '跳转链接',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`cert_id`) USING BTREE,
  KEY `idx_sort_order` (`sort_order`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='产品认证临时表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_product_channel`
--

DROP TABLE IF EXISTS `txwx_product_channel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `txwx_product_channel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `channel_id` bigint(20) NOT NULL COMMENT '渠道ID',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL,
  `del_flag` tinyint(3) unsigned zerofill NOT NULL DEFAULT '000',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_channel_id` (`channel_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COMMENT='产品渠道关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_typical_case_product`
--

DROP TABLE IF EXISTS `txwx_typical_case_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`case_product_id`) USING BTREE,
  KEY `idx_product_type` (`product_type`) USING BTREE,
  KEY `idx_sort_order` (`sort_order`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='典型案例/典型产品正式表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_typical_case_product_temp`
--

DROP TABLE IF EXISTS `txwx_typical_case_product_temp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`case_product_id`) USING BTREE,
  KEY `idx_product_type` (`product_type`) USING BTREE,
  KEY `idx_sort_order` (`sort_order`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='典型案例/典型产品临时表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_typical_customer`
--

DROP TABLE IF EXISTS `txwx_typical_customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`customer_id`) USING BTREE,
  KEY `idx_customer_name` (`customer_name`) USING BTREE,
  KEY `idx_is_show` (`is_show`) USING BTREE,
  KEY `idx_sort_order` (`sort_order`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='首页典型客户配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_typical_customer_temp`
--

DROP TABLE IF EXISTS `txwx_typical_customer_temp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `txwx_typical_customer_temp` (
  `customer_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '典型客户主键',
  `customer_name` varchar(200) DEFAULT NULL COMMENT '客户名称',
  `image_url` varchar(500) NOT NULL COMMENT '客户Logo地址',
  `jump_url` varchar(500) DEFAULT NULL COMMENT '跳转链接',
  `is_show` char(1) DEFAULT NULL COMMENT '是否显示（0-否 1-是）',
  `sort_order` int(4) DEFAULT '0' COMMENT '排序序号',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `saved_by` varchar(64) DEFAULT '' COMMENT '末次保存者',
  `saved_time` datetime DEFAULT NULL COMMENT '末次保存时间',
  `published_by` varchar(64) DEFAULT '' COMMENT '末次发布者',
  `published_time` datetime DEFAULT NULL COMMENT '末次发布时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`customer_id`) USING BTREE,
  KEY `idx_is_show` (`is_show`) USING BTREE,
  KEY `idx_sort_order` (`sort_order`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='首页典型客户配置临时表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_user_logout`
--

DROP TABLE IF EXISTS `txwx_user_logout`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `txwx_user_logout` (
  `logout_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '注销ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `apply_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `cool_end_time` datetime NOT NULL COMMENT '冷却结束时间',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '0=冷却中 1=已注销 2=已撤销',
  `create_by` varchar(64) DEFAULT '',
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(64) DEFAULT '',
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `del_flag` char(1) DEFAULT '0',
  PRIMARY KEY (`logout_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_cool_time` (`cool_end_time`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='TXWC账号注销申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_user_permission`
--

DROP TABLE IF EXISTS `txwx_user_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `txwx_user_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `relation_type` int(11) NOT NULL COMMENT '关联类型 1-产品',
  `relation_ids` varchar(500) NOT NULL COMMENT '关联id列表',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `update_by` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `del_flag` tinyint(3) unsigned zerofill NOT NULL DEFAULT '000',
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_relation_type` (`relation_type`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COMMENT='产品用户协作表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txwx_user_register`
--

DROP TABLE IF EXISTS `txwx_user_register`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `txwx_user_register` (
  `register_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '注册ID',
  `user_id` bigint(20) NOT NULL COMMENT '关联sys_user用户ID',
  `user_name` varchar(30) NOT NULL COMMENT '用户名',
  `bind_phone` varchar(11) DEFAULT NULL COMMENT '绑定手机号',
  `bind_email` varchar(50) DEFAULT NULL COMMENT '绑定邮箱',
  `register_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '0=正常 1=停用 2=注销中 3=已注销',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '0=存在 2=删除',
  `bak_phone` varchar(11) DEFAULT NULL COMMENT '备用手机号',
  `bak_email` varchar(50) DEFAULT NULL COMMENT '备用邮箱',
  PRIMARY KEY (`register_id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  UNIQUE KEY `uk_bind_phone` (`bind_phone`),
  KEY `uk_bak_phone` (`bak_phone`),
  KEY `uk_bak_email` (`bak_email`),
  KEY `uk_bind_email` (`bind_email`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COMMENT='TXWC用户注册扩展表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'wpai-prod'
--
--
-- WARNING: can't read the INFORMATION_SCHEMA.libraries table. It's most probably an old server 5.7.30-0ubuntu0.18.04.1.
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-30 11:12:54
