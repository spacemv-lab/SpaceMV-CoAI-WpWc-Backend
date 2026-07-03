package com.txwx.social.dashboard.util;

import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.api.RemoteNoticeService;
import com.ruoyi.system.api.domain.SysNoticeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 通知帮助类
 * 用于在 txwx-social-dashboard 模块中通过 Feign 发送系统通知
 */
@Slf4j
@Component
public class NoticeHelper {

    @Autowired
    private RemoteNoticeService remoteNoticeService;

    /**
     * 通知类型（统一使用 "3"=系统同步，手动/自动在内容详情中区分）
     */
    private static final String NOTICE_TYPE_SYSTEM = "3";

    /**
     * 状态：正常
     */
    private static final String STATUS_NORMAL = "0";

    private static final String STATUS_ING = "2";

    /**
     * 发送系统自动同步通知
     *
     * @param operationType 操作类型（如 "数据同步"、"文章同步"）
     * @param actionName    动作名称（如 "getYesterdayDatas完成任务"）
     * @param status       是否成功
     * @param detail        详情信息
     */
    public void sendSystemSyncNotification(String operationType, String actionName, int status, String detail) {
        try {
            SysNoticeVO vo = new SysNoticeVO();
            vo.setNoticeTitle(operationType + " - " + getStatus(status));
            vo.setNoticeType(NOTICE_TYPE_SYSTEM);
            vo.setNoticeContent(String.format("操作类型：%s\n动作：%s\n状态：%s\n时间：%s\n详情：%s",
                    operationType,
                    actionName,
                    status == 0 ? "成功" : "等待",
                    java.time.LocalDateTime.now().toString(),
                    detail));
            vo.setStatus(status == 2 ? STATUS_ING : STATUS_NORMAL);
            vo.setCreateBy("system");

            R<Integer> result = remoteNoticeService.insertSystemNotice(vo, SecurityConstants.INNER);
            log.info("发送系统通知结果: {} - {} [code={}], [msg={}]", operationType, status, result.getCode(), result.getMsg());
        } catch (Exception e) {
            log.error("发送系统通知失败: {}", e.getMessage(), e);
        }
    }

    private static String getStatus(int status) {
        switch (status) {
            case 0: return "成功";
            case 1: return "失败";
            case 2:
            default: return "任务进行中";
        }
    }

    /**
     * 发送用户操作通知（类型统一为系统同步，通过内容详情区分操作来源）
     *
     * @param operationType 操作类型
     * @param actionName    动作名称
     * @param status       是否成功
     * @param detail        详情信息
     * @param username        操作用户ID
     */
    public void sendUserOperationNotification(String operationType, String actionName, int status, String detail, String username) {
        try {
            SysNoticeVO vo = new SysNoticeVO();
            vo.setNoticeTitle(operationType + " - " + getStatus(status));
            vo.setNoticeType(NOTICE_TYPE_SYSTEM);
            vo.setNoticeContent(String.format("操作类型：%s\n动作：%s\n状态：%s\n操作用户：%s\n时间：%s\n详情：%s",
                    operationType,
                    actionName,
                    status == 0 ? "成功" : "失败",
                    username != null ? username : "unknown",
                    java.time.LocalDateTime.now().toString(),
                    detail));
            vo.setStatus(status == 2 ? STATUS_ING : STATUS_NORMAL);
            vo.setCreateBy(username != null ? username : "unknown");

            R<Integer> result = remoteNoticeService.insertUserOperNotice(vo, SecurityConstants.INNER);
            log.info("发送用户操作通知结果: {} - {} [code={}]", operationType, status, result.getCode());
        } catch (Exception e) {
            log.error("发送用户操作通知失败: {}", e.getMessage(), e);
        }
    }
}
