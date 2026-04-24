# Nacos 配置项说明

以下配置项需要在 Nacos 配置中心的手动添加到 `application-dev.yml` 中：

## 验证码配置

```yaml
txwx:
  verify:
    # 验证码过期时间（单位：分钟）
    code-expire-minutes: 5
    # 频次限制过期时间（单位：秒）
    limit-expire-seconds: 60
    # Redis key 前缀
    redis-key-prefix:
      sms-code: "txwx:verify:sms:code:"
      email-code: "txwx:verify:email:code:"
      limit: "txwx:verify:limit:"
```

## 邮箱配置（MVP阶段 - SMTP方式）

```yaml
txwx:
  email:
    # SMTP服务器主机
    host: smtp.huaweicloud.com
    # SMTP服务器端口（465为SSL端口，587为TLS端口）
    port: 465
    # SMTP用户名（发件人邮箱地址）
    username: your-email@txwx.com
    # SMTP密码或授权码（注意：不是邮箱登录密码）
    password: your-email-password-or-auth-code
    # 发件人别名
    from-alias: TXWC官方
    # 邮件主题（可选，默认：验证码）
    subject: 验证码
    # 邮件内容模板（{code} 会被替换为实际验证码）
    content-template: 您的验证码是：{code}
```

### 华为云企业邮箱配置说明

1. 登录华为云企业邮箱控制台
2. 进入「设置」->「账户设置」->「POP3/IMAP/SMTP」
3. 开启「POP3/IMAP/SMTP服务」
4. 复制「SMTP服务器地址」和「端口号」
5. 生成并复制「授权码」（不是登录密码）

### 其他邮箱服务商配置参考

| 服务商 | SMTP主机 | 端口 | 说明 |
|--------|----------|------|------|
| 阿里云企业邮箱 | smtp.mxhichina.com | 465 | 同样需要使用授权码 |
| 腾讯企业邮箱 | smtp.exmail.qq.com | 465 | 同样需要使用授权码 |

## 注意事项

1. **安全建议**：生产环境建议使用环境变量或密钥管理服务存储密码
   ```yaml
   password: ${SMTP_PASSWORD:}  # 从环境变量读取
   ```

2. **SSL/TLS**：配置中已启用 SSL (`sslEnable: true`)，请确保使用对应的安全端口

3. **频次限制**：默认 1 分钟内同一邮箱只能发送 1 次验证码，防止恶意刷验证码

4. **验证码有效期**：默认 5 分钟内有效

## 短信配置（阿里云）

```yaml
txwx:
  sms:
    # 短信服务商（当前仅支持 aliyun）
    vendor: aliyun
    # 阿里云配置
    aliyun:
      # Access Key ID（从阿里云控制台获取）
      access-key-id: your-access-key-id
      # Access Key Secret（从阿里云控制台获取）
      access-key-secret: your-access-key-secret
    # 短信签名（需在阿里云控制台配置并审核通过）
    sign-name: 【TXWC科技】
    # 短信模板Code（需在阿里云控制台配置并审核通过）
    template-code: SMS_123456789
```

### 阿里云短信配置说明

1. 登录[阿里云短信服务控制台](https://dysms.console.aliyun.com/)
2. 获取 AccessKey ID 和 AccessKey Secret（在**AccessKey 管理**页面）
3. 在**签名管理**中添加短信签名（如：【TXWC科技】）
4. 在**模板管理**中添加短信模板（如：您的验证码是：${code}，3分钟内有效）
5. 复制模板 Code（格式：SMS_xxxxxxx）

### 其他服务商（预留）

| 服务商 | 配置前缀 | 说明 |
|--------|----------|------|
| 腾讯云 | `txwx.sms.tencent` | 待实现 |

## 邮件模板配置说明

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `txwx.email.subject` | 验证码 | 邮件主题 |
| `txwx.email.content-template` | 您的验证码是：{code} | 邮件内容模板，`{code}` 会被替换为实际验证码 |
