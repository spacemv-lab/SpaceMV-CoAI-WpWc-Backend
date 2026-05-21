package com.ruoyi.iam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 存量用户迁移导入请求（内部接口）
 *
 * @author txwx
 */
@Data
@Schema(description = "存量用户迁移导入请求对象")
public class InnerUserImportRequest
{
    @Schema(description = "用户名（CRM 原始用户名）")
    private String username;

    @Schema(description = "密码（CRM BCrypt 哈希）")
    private String passwordHash;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "显示名")
    private String displayName;

    @Schema(description = "头像 URL")
    private String avatarUrl;

    @Schema(description = "产品线标识（为空时默认 spacemv-coai）")
    private String productLine;

    @Schema(description = "系统模块用户 ID（sys_user.user_id），用于创建 iam_user_product 映射")
    private Long productUserId;
}
