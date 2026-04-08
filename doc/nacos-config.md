# Nacos 配置说明

本文档说明三个微服务模块的 Nacos 配置。

---

## 一、txwx-social-crm 配置

**Data ID**: `txwx-social-crm-dev.yml`
**Group**: `DEFAULT_GROUP`

```yaml
# Spring 配置
spring:
  datasource:
    dynamic:
      primary: master
      strict: false
      datasource:
        master:
          driver-class-name: com.mysql.cj.jdbc.Driver
          url: jdbc:mysql://***REMOVED***:3306/ry-cloud2?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
          username: root
          password: root@123
      druid:
        initial-size: 5
        min-idle: 5
        max-active: 20
        max-wait: 60000
        time-between-eviction-runs-millis: 60000
        min-evictable-idle-time-millis: 300000
        validation-query: SELECT 1
        test-while-idle: true
        test-on-borrow: false
        test-on-return: false

# MyBatis Plus 配置
mybatis-plus:
  mapper-locations: classpath*:mapper/**/*Mapper.xml
  type-aliases-package: com.txwx.social.crm.domain
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

# 服务配置
server:
  port: 8081

spring:
  application:
    name: txwx-social-crm

# Nacos 配置
spring:
  cloud:
    nacos:
      discovery:
        server-addr: ***REMOVED***:8848
        namespace: ***REMOVED***
      config:
        server-addr: ***REMOVED***:8848
        namespace: ***REMOVED***
        file-extension: yaml
```

---

## 二、txwx-sync-center 配置

**Data ID**: `txwx-sync-center-dev.yml`
**Group**: `DEFAULT_GROUP`

```yaml
# Spring 配置
spring:
  datasource:
    dynamic:
      primary: master
      strict: false
      datasource:
        master:
          driver-class-name: com.mysql.cj.jdbc.Driver
          url: jdbc:mysql://***REMOVED***:3306/ry-cloud2?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
          username: root
          password: root@123
      druid:
        initial-size: 5
        min-idle: 5
        max-active: 20
        max-wait: 60000
        time-between-eviction-runs-millis: 60000
        min-evictable-idle-time-millis: 300000
        validation-query: SELECT 1
        test-while-idle: true
        test-on-borrow: false
        test-on-return: false

# MyBatis Plus 配置
mybatis-plus:
  mapper-locations: classpath*:mapper/**/*Mapper.xml
  type-aliases-package: com.txwx.sync.center.domain
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

# 服务配置
server:
  port: 8082

spring:
  application:
    name: txwx-sync-center

# Nacos 配置
spring:
  cloud:
    nacos:
      discovery:
        server-addr: ***REMOVED***:8848
        namespace: ***REMOVED***
      config:
        server-addr: ***REMOVED***:8848
        namespace: ***REMOVED***
        file-extension: yaml

# Remote 配置（调用其他模块）
ruoyi:
  remote:
    txwx-social-dashboard:
      name: txwx-social-dashboard
      context: /remote
```

---

## 三、txwx-social-dashboard 配置

**Data ID**: `txwx-social-dashboard.yml`
**Group**: `DEFAULT_GROUP`

```yaml
# Spring 配置
spring:
  datasource:
    dynamic:
      primary: clickhouse
      strict: false
      datasource:
        clickhouse:
          driver-class-name: ru.yandex.clickhouse.ClickHouseDriver
          url: jdbc:clickhouse://10.0.3.4:8124/wcai
          username: admin
          password: 123456

# 服务配置
server:
  port: 8083

spring:
  application:
    name: txwx-social-dashboard

# Nacos 配置
spring:
  cloud:
    nacos:
      discovery:
        server-addr: ***REMOVED***:8848
        namespace: ***REMOVED***
      config:
        server-addr: ***REMOVED***:8848
        namespace: ***REMOVED***
        file-extension: yaml

# Remote 配置（提供给其他模块调用）
ruoyi:
  remote:
    txwx-social-crm:
      name: txwx-social-crm
      context: /remote
    txwx-sync-center:
      name: txwx-sync-center
      context: /remote
```

---

## 四、Nacos 配置说明

### 1. 配置文件说明

| 配置文件 | 用途 | Data ID |
|---------|------|---------|
| txwx-social-crm.yml | 内容管理服务配置 | txwx-social-crm.yml |
| txwx-sync-center.yml | 同步任务中心配置 | txwx-sync-center.yml |
| txwx-social-dashboard.yml | 运营数据分析服务配置 | txwx-social-dashboard.yml |

### 2. 数据源说明

| 服务 | MySQL 数据源 | ClickHouse 数据源 |
|-----|-------------|------------------|
| txwx-social-crm | ry-cloud2 (master) | - |
| txwx-sync-center | ry-cloud2 (master) | - |
| txwx-social-dashboard | - | wcai |

### 3. 服务端口

| 服务 | 端口 |
|-----|------|
| txwx-social-crm | 8081 |
| txwx-sync-center | 8082 |
| txwx-social-dashboard | 8083 |
