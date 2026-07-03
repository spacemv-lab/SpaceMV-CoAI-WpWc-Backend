package com.txwx.social.dashboard.util;

import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.api.RemoteNoticeService;
import com.ruoyi.system.api.domain.SysNoticeVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * NoticeHelper 单元测试
 * 验证 Feign 调用逻辑和通知类型正确性
 */
@ExtendWith(MockitoExtension.class)
class NoticeHelperTest {

    @Mock
    private RemoteNoticeService remoteNoticeService;

    @InjectMocks
    private NoticeHelper noticeHelper;

    private final String OPERATION_TYPE = "数据同步";
    private final String ACTION_NAME = "getYesterdayDatas";
    private final String DETAIL = "同步完成";
    private final String USER_ID = "user123";

    @Test
    void sendSystemSyncNotification_shouldCallFeignWithSystemType() {
        when(remoteNoticeService.insertSystemNotice(any(SysNoticeVO.class), eq(SecurityConstants.FROM_SOURCE)))
                .thenReturn(R.ok(1));

        noticeHelper.sendSystemSyncNotification(OPERATION_TYPE, ACTION_NAME, 0, DETAIL);

        ArgumentCaptor<SysNoticeVO> captor = ArgumentCaptor.forClass(SysNoticeVO.class);
        verify(remoteNoticeService).insertSystemNotice(
                captor.capture(),
                eq(SecurityConstants.FROM_SOURCE));

        SysNoticeVO vo = captor.getValue();
        assertEquals(OPERATION_TYPE + " - 成功", vo.getNoticeTitle());
        assertEquals("3", vo.getNoticeType());            // System Sync
        assertEquals(0, vo.getNoticeContent().split("\n").length);
        assertTrue(vo.getNoticeContent().contains(OPERATION_TYPE));
        assertTrue(vo.getNoticeContent().contains(ACTION_NAME));
        assertEquals(0, vo.getStatus().compareTo("0"));    // STATUS_NORMAL
        assertEquals("system", vo.getCreateBy());
    }

    @Test
    void sendSystemSyncNotification_shouldShowFailureWhenFailed() {
        when(remoteNoticeService.insertSystemNotice(any(SysNoticeVO.class), eq(SecurityConstants.FROM_SOURCE)))
                .thenReturn(R.ok(1));

        noticeHelper.sendSystemSyncNotification(OPERATION_TYPE, ACTION_NAME, 1, DETAIL);

        ArgumentCaptor<SysNoticeVO> captor = ArgumentCaptor.forClass(SysNoticeVO.class);
        verify(remoteNoticeService).insertSystemNotice(
                captor.capture(),
                eq(SecurityConstants.FROM_SOURCE));

        SysNoticeVO vo = captor.getValue();
        assertEquals(OPERATION_TYPE + " - 失败", vo.getNoticeTitle());
        assertTrue(vo.getNoticeContent().contains("失败"));
    }

    @Test
    void sendUserOperationNotification_shouldCallFeignWithUserType() {
        when(remoteNoticeService.insertUserOperNotice(any(SysNoticeVO.class), eq(SecurityConstants.FROM_SOURCE)))
                .thenReturn(R.ok(1));

        noticeHelper.sendUserOperationNotification(OPERATION_TYPE, ACTION_NAME, 0, DETAIL, USER_ID);

        ArgumentCaptor<SysNoticeVO> captor = ArgumentCaptor.forClass(SysNoticeVO.class);
        verify(remoteNoticeService).insertUserOperNotice(
                captor.capture(),
                eq(SecurityConstants.FROM_SOURCE));

        SysNoticeVO vo = captor.getValue();
        assertEquals(OPERATION_TYPE + " - 成功", vo.getNoticeTitle());
        assertEquals("4", vo.getNoticeType());            // User Op
        assertTrue(vo.getNoticeContent().contains(USER_ID));
        assertEquals(USER_ID, vo.getCreateBy());
    }

    @Test
    void sendUserOperationNotification_shouldHandleNullUserId() {
        when(remoteNoticeService.insertUserOperNotice(any(SysNoticeVO.class), eq(SecurityConstants.FROM_SOURCE)))
                .thenReturn(R.ok(1));

        noticeHelper.sendUserOperationNotification(OPERATION_TYPE, ACTION_NAME, 0, DETAIL, null);

        ArgumentCaptor<SysNoticeVO> captor = ArgumentCaptor.forClass(SysNoticeVO.class);
        verify(remoteNoticeService).insertUserOperNotice(
                captor.capture(),
                eq(SecurityConstants.FROM_SOURCE));

        SysNoticeVO vo = captor.getValue();
        assertEquals("unknown", vo.getCreateBy());
        assertTrue(vo.getNoticeContent().contains("unknown"));
    }

    @Test
    void sendSystemSyncNotification_shouldNotThrowOnFallback() {
        when(remoteNoticeService.insertSystemNotice(any(SysNoticeVO.class), eq(SecurityConstants.FROM_SOURCE)))
                .thenReturn(R.ok(-1, "降级"));

        // 不应抛出异常，仅返回降级结果
        assertDoesNotThrow(() ->
                noticeHelper.sendSystemSyncNotification(OPERATION_TYPE, ACTION_NAME, 1, "降级详情"));
    }
}
