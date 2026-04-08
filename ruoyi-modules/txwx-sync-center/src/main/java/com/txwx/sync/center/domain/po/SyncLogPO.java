package com.txwx.sync.center.domain.po;

import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 同步日志表
 *
 * @author txwx
 * @date 2026-04-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SyncLogPO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 渠道类型
     */
    private String channelType;

    /**
     * 账号ID
     */
    private Long accountId;

    /**
     * 数据类型（users, article, read_daily等）
     */
    private String dataType;

    /**
     * 数据条数
     */
    private Integer dataCount;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 状态（0-成功 1-失败）
     */
    private String status;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 删除标志(0代表存在 1代表删除)
     */
    private String delFlag;
}
