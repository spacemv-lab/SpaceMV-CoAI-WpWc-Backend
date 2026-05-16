# 贡献指南

感谢您考虑为 SpaceMV-CoAI-Wp/Wc Backend 贡献代码！以下指南将帮助您了解如何参与本项目。

## 行为准则

请保持尊重和专业的交流态度。我们欢迎所有建设性的讨论和贡献。

## 如何贡献

### 报告问题 (Issues)

1. 在提交 Issue 前，请先搜索是否已有类似问题
2. 使用 Issue 模板（如适用）并填写必要信息
3. 清楚描述问题的复现步骤、预期行为和实际行为
4. 附上相关日志、截图或代码片段

### 提交代码 (Pull Requests)

1. **Fork 本仓库** — 点击右上角的 Fork 按钮
2. **创建分支** — 从 `main` 分支创建新分支：
   ```bash
   git checkout -b feature/your-feature-name
   # 或
   git checkout -b fix/your-bug-fix
   ```
3. **编写代码** — 遵循项目编码规范：
   - 保持代码风格与现有代码一致
   - 添加必要的单元测试
   - 确保所有测试通过
   - 更新相关文档
4. **提交更改** — 使用清晰的 Commit Message：
   ```bash
   git commit -m 'feat: Add some AmazingFeature'
   git commit -m 'fix: Resolve issue with...'
   ```
5. **推送分支**：
   ```bash
   git push origin feature/your-feature-name
   ```
6. **提交 Pull Request** — 使用 PR 模板并详细描述更改内容

### 代码审查

- 所有 PR 需经过至少一位维护者 Review 后才能合并
- 请积极响应 Review 中的反馈意见
- 合入方式：Squash and Merge

## 开发规范

### 分支命名

| 类型 | 格式 | 示例 |
|:---|:---|:---|
| 新功能 | `feature/description` | `feature/user-avatar-upload` |
| 修复 | `fix/description` | `fix/login-redirect-error` |
| 优化 | `refactor/description` | `refactor/api-response-format` |
| 文档 | `docs/description` | `docs/api-usage-guide` |

### Commit Message 规范

请遵循 [Conventional Commits](https://www.conventionalcommits.org/) 规范：

```
<type>: <description>

[optional body]
```

| Type | 说明 |
|:---|:---|
| `feat` | 新功能 |
| `fix` | 修复 Bug |
| `docs` | 文档变更 |
| `refactor` | 代码重构（无功能变更） |
| `test` | 测试相关 |
| `chore` | 构建/工具/依赖相关 |

### 模块开发

- 新微服务请放置在 `ruoyi-modules/` 下
- 公共服务组件请放置在 `ruoyi-common/` 下
- 遵循项目现有的包命名规范

### 安全须知

> **⚠️ 严禁** 在代码中硬编码任何敏感信息，包括但不限于：
> - 密钥、密码、Token
> - 数据库连接串
> - API Key、Secret
> - 内网地址
>
> 所有敏感配置必须通过环境变量或 Nacos 配置中心注入。

## 测试

- 提交代码前请确保项目可以正常构建：`mvn clean install`
- 新功能应包含对应的单元测试

## 文档更新

- 新功能或 API 变更需同步更新 README.md 和 README-EN.md
- 重大变更应在 PR 描述中详细说明变更内容

---

再次感谢您的贡献！🎉
