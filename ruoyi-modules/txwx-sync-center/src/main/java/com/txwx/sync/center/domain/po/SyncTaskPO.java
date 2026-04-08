package com.txwx.sync.center.domain.po;

import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 同步任务表
 *
 * @author txwx
 * @date 2026-04-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SyncTaskPO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 渠道类型（wechat, douyin, xiaohongshu）
     */
    private String channelType;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 渠道ID
     */
    private Long channelId;

    /**
     * 账号ID
     */
    private Long accountId;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 任务状态（0-待执行 1-执行中 2-已完成 3-失败）
     */
    private String taskStatus;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 同步条数
     */
    private Integer syncCount;

    /**
     * 删除标志(0代表存在 1代表删除)
     */
    private String delFlag;
}
