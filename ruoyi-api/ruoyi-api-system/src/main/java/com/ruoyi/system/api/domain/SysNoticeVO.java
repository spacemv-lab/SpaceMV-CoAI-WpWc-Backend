package com.ruoyi.system.api.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 通知公告轻量 DTO（仅用于内部 Feign 调用）
 * 
 * @author txwx
 */
@Data
public class SysNoticeVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long noticeId;

    /** 通知标题 */
    private String noticeTitle;

    /** 通知类型（1=通知，2=公告） */
    private String noticeType;

    /** 备注 */
    private String remark;

    /** 状态（0=正常，1=隐藏） */
    private String status;

    /** 创建者 */
    private String createBy;

    /** 内容 */
    private String noticeContent;
}
