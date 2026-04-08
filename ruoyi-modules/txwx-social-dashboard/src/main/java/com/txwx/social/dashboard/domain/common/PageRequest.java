package com.txwx.social.dashboard.domain.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequest {
    /**
     * 当前页码
     */
    private Integer pageNum=1;
    /**
     * 每页数量
     */
    private Integer pageSize=10;
}
