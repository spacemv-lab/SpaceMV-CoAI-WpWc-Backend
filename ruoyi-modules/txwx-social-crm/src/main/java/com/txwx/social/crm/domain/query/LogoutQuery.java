package com.txwx.social.crm.domain.query;

import lombok.Data;

import javax.validation.constraints.Min;

/**
 * 注销申请查询参数
 */
@Data
public class LogoutQuery {
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "页大小不能小于1")
    private Integer pageSize = 10;

    private Long userId;

    private String status;
}
