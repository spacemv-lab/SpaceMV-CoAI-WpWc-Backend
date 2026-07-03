package com.txwx.social.dashboard.feign;

import com.ruoyi.system.api.RemoteNoticeService;
import com.ruoyi.system.api.domain.SysNoticeVO;
import com.ruoyi.system.api.factory.RemoteNoticeFallbackFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * RenuNoticeService 降级测试
 * 验证 FallbackFactory 返回降级结果而非抛出异常
 */
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class RemoteNoticeFallbackFactoryTest {

    @InjectMocks
    private RemoteNoticeFallbackFactory fallbackFactory = new RemoteNoticeFallbackFactory();

    @Test
    void fallbackShouldReturnOkWithMinusOne() {
        Throwable cause = new RuntimeException("服务不可用");
        RemoteNoticeService service = fallbackFactory.create(cause);

        SysNoticeVO vo = new SysNoticeVO();
        vo.setNoticeTitle("测试");

        // 系统同步通知降级
        var result1 = service.insertSystemNotice(vo, "inner");
        assertNotNull(result1);
        assertEquals(-1, result1.getCode());
        assertTrue(result1.getMsg().contains("降级"));

        // 用户操作通知降级
        var result2 = service.insertUserOperNotice(vo, "inner");
        assertNotNull(result2);
        assertEquals(-1, result2.getCode());
        assertTrue(result2.getMsg().contains("降级"));
    }

    @Test
    void fallbackShouldPreserveExceptionMessage() {
        String errorMsg = "连接超时: 10.0.10.102:8848";
        RemoteNoticeService service = fallbackFactory.create(new RuntimeException(errorMsg));

        SysNoticeVO vo = new SysNoticeVO();
        var result = service.insertSystemNotice(vo, "inner");

        assertTrue(result.getMsg().contains(errorMsg));
    }
}
