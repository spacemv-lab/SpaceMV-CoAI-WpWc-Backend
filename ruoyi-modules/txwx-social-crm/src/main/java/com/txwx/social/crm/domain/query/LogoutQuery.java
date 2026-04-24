package com.txwx.social.crm.domain.query;

import lombok.Data;

import javax.validation.constraints.Min;

/**
 * 注销申请查询参数
 */
@Data
public class LogoutQuery {

    private Long userId;

    private String status;
}
