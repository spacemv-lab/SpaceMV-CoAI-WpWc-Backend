package com.ruoyi.iam.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 白名单校验请求 DTO
 *
 * @author txwx
 */
@Data
@Schema(description = "白名单校验请求对象")
public class WhitelistCheckRequest {

    @Schema(description = "账号（手机号/邮箱）")
    private String account;

    @Schema(description = "产品线标识（兼容 businessLine 旧字段名）")
    @JsonAlias({"businessLine", "productLine"})
    private String productLine;
}
