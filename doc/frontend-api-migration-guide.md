# 前端接口迁移指南

## 概述

本文档guide用于将前端接口从源服务（http://nacos-server:9204）迁移到目标服务（http://localhost:9205）。

**源服务模块**：02--【自媒体】--产品、02--【自媒体】--平台  
**目标服务模块**：03--【CRM】--产品管理、04--【CRM】--渠道管理、07--【CRM】--产品渠道关联、05--【CRM】--账号管理

---

## 服务信息

| 项目 | 源服务 | 目标服务 |
|------|--------|----------|
| 域名 | nacos-server:9204 | localhost:9205 |
| 接口文档 | http://nacos-server:9204/swagger-ui/index.html | http://localhost:9205/swagger-ui/index.html |

---

## 接口路径映射

### 产品管理（原自媒体-产品）

| 原接口 | 新接口 | 方法 | 说明 |
|--------|--------|------|------|
| `/api/mediaProduct/add` | `/api/crm/product` | POST | 新增产品 |
| `/api/mediaProduct/update` | `/api/crm/product` | PUT | 更新产品 |
| `/api/mediaProduct/delete` | `/api/crm/product/{ids}` | DELETE | 删除产品（批量） |
| `/api/mediaProduct/getOne` | `/api/crm/product/view/getOne` | GET | 获取产品详情 |
| `/api/mediaProduct/list` | `/api/crm/product/view/list` | POST | 产品列表 |

**请求参数变更**：
- `MediaProduct` → `TxwxProductPO`
- `id` (int64): ID
- `name` (string) → `productName` (string): 产品名称
- `description` (string) → `productDesc` (string): 产品描述

### 平台管理（原自媒体-平台）→ 渠道管理

| 原接口 | 新接口 | 方法 | 说明 |
|--------|--------|------|------|
| `/api/mediaPlatform/saveUpdate` | `/api/crm/channel` | POST | 新增渠道 |
| `/api/mediaPlatform/saveUpdate` | `/api/crm/channel` | PUT | 更新渠道 |
| `/api/mediaPlatform/delete` | `/api/crm/channel/{ids}` | DELETE | 删除渠道 |
| `/api/mediaPlatform/list` | `/api/crm/channel/view/list` | POST | 渠道列表 |

**请求参数变更**：
- `MediaPlatform` → `TxwxChannelPO`
- `id` (int64): ID
- `name` (string) → `channelName` (string): 渠道名称
- `productId` (int64): 产品ID
- `appId` (string) → `appid` (string): 应用ID
- `secret` (string): 密钥
- `remark` (string): 备注

### 账号管理（新增模块）

| 原接口 | 新接口 | 方法 | 说明 |
|--------|--------|------|------|
| - | `/api/crm/account` | POST | 新增账号 |
| - | `/api/crm/account` | PUT | 更新账号 |
| - | `/api/crm/account/{ids}` | DELETE | 删除账号（批量） |
| - | `/api/crm/account/view/getOne` | GET | 获取账号详情 |
| - | `/api/crm/account/view/list` | POST | 账号列表 |
| - | `/api/crm/account/byProduct/{productId}` | GET | 根据产品ID获取账号列表 |
| - | `/api/crm/account/byChannel/{channelId}` | GET | 根据渠道ID获取账号列表 |

**请求参数**：
- `TxwxAccountPO` 对象
- `channelId` (int64): 渠道ID
- `productId` (int64): 产品ID
- `accountName` (string): 账号名称
- `accountNo` (string): 账号编号
- `appid` (string): 应用ID
- `secret` (string): 密钥
- `token` (string): 令牌
- `encodingAesKey` (string): 消息加密集钥
- `status` (string): 状态

### 产品渠道关联（新增模块）

| 原接口 | 新接口 | 方法 | 说明 |
|--------|--------|------|------|
| - | `/api/crm/productChannel` | POST | 新增关联 |
| - | `/api/crm/productChannel` | PUT | 更新关联 |
| - | `/api/crm/productChannel/{ids}` | DELETE | 删除关联（批量） |
| - | `/api/crm/productChannel/{id}` | GET | 获取关联详情 |
| - | `/api/crm/productChannel/list` | POST | 关联列表 |
| - | `/api/crm/productChannel/productIds/{channelId}` | GET | 获取渠道关联的产品ID列表 |
| - | `/api/crm/productChannel/channelIds/{productId}` | GET | 获取产品关联的渠道ID列表 |

**请求参数**：
- `TxwxProductChannelPO` 对象
- `productId` (int64): 产品ID
- `channelId` (int64): 渠道ID

---

## 详细接口对比

### 1. 产品管理接口

#### 1.1 新增产品

**源服务（旧）**
```
POST /api/mediaProduct/add
Content-Type: application/json

{
  "name": "产品名称",
  "description": "产品描述",
  ...
}
```

**目标服务（新）**
```
POST /api/crm/product
Content-Type: application/json

{
  "productName": "产品名称",
  "productDesc": "产品描述",
  ...
}
```

#### 1.2 更新产品

**源服务（旧）**
```
POST /api/mediaProduct/update
Content-Type: application/json

{
  "id": 1,
  "name": "更新后的产品名称",
  "description": "更新后的产品描述",
  ...
}
```

**目标服务（新）**
```
PUT /api/crm/product
Content-Type: application/json

{
  "id": 1,
  "productName": "更新后的产品名称",
  "productDesc": "更新后的产品描述",
  ...
}
```

#### 1.3 删除产品

**源服务（旧）**
```
DELETE /api/mediaProduct/delete
Content-Type: application/json

[1, 2, 3]  // 批量删除的ID数组
```

**目标服务（新）**
```
DELETE /api/crm/product/{ids}
// IDS作为路径参数: DELETE /api/crm/product/1,2,3
```

#### 1.4 获取产品详情

**源服务（旧）**
```
GET /api/mediaProduct/getOne?id=1
```

**目标服务（新）**
```
GET /api/crm/product/view/getOne?id=1
```

#### 1.5 产品列表

**源服务（旧）**
```
POST /api/mediaProduct/list
// 无请求体
```

**目标服务（新）**
```
POST /api/crm/product/view/list
// 可选请求体用于筛选
```

### 2. 渠道管理接口（原平台管理）

#### 2.1 新增/更新渠道

**源服务（旧）**
```
POST /api/mediaPlatform/saveUpdate
Content-Type: application/json

{
  "id": 1,
  "name": "平台名称",
  "productId": 1,
  "appId": "your_app_id",
  "secret": "your_secret",
  "remark": "备注"
}
```

**目标服务（新）**
```
POST /api/crm/channel
Content-Type: application/json

{
  "id": 1,
  "channelName": "平台名称",
  "productId": 1,
  "appid": "your_app_id",
  "secret": "your_secret",
  "remark": "备注"
}
```

### 3. 账号管理接口（新增）

#### 3.1 新增账号

```
POST /api/crm/account
Content-Type: application/json

{
  "channelId": 1,
  "productId": 1,
  "accountName": "账号名称",
  "accountNo": "账号编号",
  "appid": "your_app_id",
  "secret": "your_secret",
  "token": "your_token",
  "encodingAesKey": "your_encoding_aes_key",
  "status": "1"
}
```

#### 3.2 账号列表（按产品查询）

```
GET /api/crm/account/byProduct/{productId}
```

#### 3.3 账号列表（按渠道查询）

```
GET /api/crm/account/byChannel/{channelId}
```

### 4. 产品渠道关联接口（新增）

#### 4.1 新增关联

```
POST /api/crm/productChannel
Content-Type: application/json

{
  "productId": 1,
  "channelId": 2
}
```

#### 4.2 获取产品关联的渠道ID列表

```
GET /api/crm/productChannel/channelIds/{productId}
```

#### 4.3 获取渠道关联的产品ID列表

```
GET /api/crm/productChannel/productIds/{channelId}
```

---

## 数据模型对比

### MediaProduct → TxwxProductPO

| 字段 | 类型 | 说明 | 变更 |
|------|------|------|------|
| id | int64 | ID | 无 |
| uuid | string | uuid | 无 |
| createBy | int64 | 创建人ID | 无 |
| createTime | string | 创建时间 | 无 |
| createByName | string | 创建人名称 | 无 |
| modifyBy | int64 | 修改人ID | 无 |
| modifyTime | string | 修改时间 | 无 |
| modifyByName | string | 修改人名称 | 无 |
| name | string | 产品名称 | → `productName` |
| description | string | 产品描述 | → `productDesc` |
| isDelete | int32 | 是否删除 | → `delFlag` (string) |

### MediaPlatform → TxwxChannelPO

| 字段 | 类型 | 说明 | 变更 |
|------|------|------|------|
| id | int64 | ID | 无 |
| uuid | string | uuid | 无 |
| createBy | int64 | 创建人ID | 无 |
| createTime | string | 创建时间 | 无 |
| createByName | string | 创建人名称 | 无 |
| modifyBy | int64 | 修改人ID | 无 |
| modifyTime | string | 修改时间 | 无 |
| modifyByName | string | 修改人名称 | 无 |
| name | string | 平台名称 | → `channelName` |
| productId | int64 | 产品id | 无 |
| appId | string | appId | → `appid` |
| secret | string | secret | 无 |
| isDelete | int32 | 是否删除 | → `delFlag` (string) |
| syncStatus | int32 | 同步状态 | 已移除 |
| remark | string | 备注 | 无 |

### TxwxAccountPO（新增）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int64 | ID |
| channelId | int64 | 渠道ID |
| productId | int64 | 产品ID |
| accountName | string | 账号名称 |
| accountNo | string | 账号编号 |
| appid | string | 应用ID |
| secret | string | 密钥 |
| token | string | 令牌 |
| encodingAesKey | string | 消息加密集钥 |
| status | string | 状态 |
| lastSyncTime | string | 最后同步时间 |
| remark | string | 备注 |
| delFlag | string | 删除标记 |

### TxwxProductChannelPO（新增）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int64 | ID |
| productId | int64 | 产品ID |
| channelId | int64 | 渠道ID |
| remark | string | 备注 |
| delFlag | string | 删除标记 |

---

## 迁移步骤

### 1. 更新API基础URL

```javascript
// 旧
const API_BASE_URL = 'http://nacos-server:9204/api';

// 新
const API_BASE_URL = 'http://localhost:9205/api';
```

### 2. 更新接口路径

按照上方的映射表更新所有接口路径。

### 3. 更新请求参数

- 将 `name` 字段改为 `productName`
- 将 `description` 字段改为 `productDesc`
- 将 `appId` 字段改为 `appid`

### 4. 更新响应处理

响应格式保持一致，均为 `AjaxResult` 类型：

```javascript
{
  "error": false,
  "warn": false,
  "success": true,
  "empty": false,
  // ... 其他数据
}
```

---

## 注意事项

1. **删除接口参数变更**：删除接口从请求体传递ID数组改为路径参数传递
2. **新增查询接口**：目标服务提供了根据产品ID或渠道ID查询账号的接口
3. **新增关联接口**：目标服务提供了产品与渠道的关联查询接口
4. **字段类型变更**：`isDelete` 和 `delFlag` 字段类型从 `int32` 变为 `string`
5. **PUT请求方法**：更新接口使用 PUT 方法（旧接口使用 POST）

---

## 测试检查清单

- [ ] 产品新增功能正常
- [ ] 产品更新功能正常
- [ ] 产品删除功能正常（路径参数测试）
- [ ] 产品列表查询功能正常
- [ ] 渠道新增/更新功能正常
- [ ] 渠道删除功能正常
- [ ] 账号新增/更新/删除功能正常
- [ ] 账号按产品查询功能正常
- [ ] 账号按渠道查询功能正常
- [ ] 产品渠道关联功能正常
- [ ] 关联关系查询功能正常
