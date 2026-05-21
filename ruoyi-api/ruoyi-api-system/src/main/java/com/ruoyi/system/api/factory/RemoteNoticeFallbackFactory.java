package com.ruoyi.system.api.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.api.RemoteNoticeService;
import com.ruoyi.system.api.domain.SysNoticeVO;

/**
 * 公告服务降级处理
 * - 降级返回 R.ok(-1, "通知写入降级") 而非抛异常
 * - 确保通知失败不影响主业务流程
 * 
 * @author txwx
 */
@Component
public class RemoteNoticeFallbackFactory implements FallbackFactory<RemoteNoticeService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteNoticeFallbackFactory.class);

    @Override
    public RemoteNoticeService create(Throwable throwable) {
        log.error("公告服务调用失败: {}", throwable.getMessage());
        return new RemoteNoticeService() {
            @Override
            public R<Integer> insertSystemNotice(SysNoticeVO vo, String source) {
                return R.ok(-1, "通知写入降级: " + throwable.getMessage());
            }

            @Override
            public R<Integer> insertUserOperNotice(SysNoticeVO vo, String source) {
                return R.ok(-1, "通知写入降级: " + throwable.getMessage());
            }
        };
    }
}
