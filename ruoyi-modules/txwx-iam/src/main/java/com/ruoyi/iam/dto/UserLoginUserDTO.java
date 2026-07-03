/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 登录用户信息 DTO
 *
 * @author txwx
 */
@Data
@Schema(description = "登录用户信息对象")
public class UserLoginUserDTO
{
    @Schema(description = "用户 ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "显示名")
    private String displayName;

    @Schema(description = "头像 URL")
    private String avatarUrl;

    @Schema(description = "用户状态：0=正常")
    private String status;

    @Schema(description = "删除状态：0=存在 1=冷静期 2=已注销")
    private String deleteStatus;

    @Schema(description = "通道列表")
    private List<ChannelResponse> channels;

    /**
     * 通道信息 DTO
     */
    @Data
    @Schema(description = "通道信息对象")
    public static class ChannelResponse
    {
        @Schema(description = "通道 ID")
        private Long id;

        @Schema(description = "通道类型：phone / email")
        private String channelType;

        @Schema(description = "通道账号（脱敏）")
        private String channelAccount;

        @Schema(description = "通道账号（原始值，前端自行脱敏处理）")
        private String rawChannelAccount;

        @Schema(description = "是否主通道：1=是 0=否")
        private String isPrimary;

        @Schema(description = "绑定时间")
        private String bindTime;

        @Schema(description = "状态：0=正常")
        private String status;
    }
}
