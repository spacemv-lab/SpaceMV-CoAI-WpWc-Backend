# 业务代码合并完成 — 最终状态

## 变更概况

| 类型 | 数量 |
|------|------|
| 修改文件 | 18 个（diff 变更已应用） |
| 删除文件 | 71 个（旧包 `social.dashboard/` 已移除） |
| 新增文件 | 27 个（新模块 + API 接口 + 新包） |

## 已完成操作

### 1. Diff 合并完成 ✅
- **pom.xml** (根 + 子模块): Java 17, Lombok 升级, txwx-iam 模块注册
- **Gateway**: AuthFilter + IgnoreWhiteProperties 更新, 新增 IAM 认证过滤
- **API 层**: RemoteIamAuthService, RemoteNoticeService 等 Feign 接口
- **系统模块**: SysUserController, SysNoticeServiceImpl, Mapper 更新
- **txwx-iam 模块**: 全新 88 个文件 (Auth/User/Channel/Register/Logout/Deactivate)

### 2. 包名重命名：`social.dashboard` → `social/dashboard` ✅
- 旧包 71 个文件已用 `git rm` 删除（已 staged）
- 新包 96 个文件已就位（untracked，包含 diff 新增的 entity/condition/enums/vo/exception 等）

### 3. 手动修复 ✅
- **SyncDataService.java**: 添加了 `NoticeHelper` 注入和 `noticeHelper.sendUserOperationNotification()` 调用
- **.gitignore**: 添加 3 条新规则
- **CRM 控制器删除**: 2 个控制器已按 diff 删除（功能移至 txwx-iam）

### 4. 冲突处理 ✅
- **bootstrap.yml**: 跳过所有 Nacos namespace 硬编码，保留开源版 env var 方案
- **所有 .rej 文件**: 已全部清除（12 个）

## 未完成事项

- **尚未编译**：按照用户默认策略，等待用户确认后编译
- **尚未提交**：变更涉及 18 修改 + 71 删除(staged) + 27 新增(untracked)，需要用户确认后再 commit
