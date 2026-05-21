package com.ruoyi.iam.dto;

import lombok.Data;

/**
 * 注销申请查询参数
 */
@Data
public class LogoutQuery {

    private Long userId;

    private String status;
}
