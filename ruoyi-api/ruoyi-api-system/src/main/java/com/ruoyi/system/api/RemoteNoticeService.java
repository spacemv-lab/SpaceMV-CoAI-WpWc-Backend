package com.ruoyi.system.api;

import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.constant.ServiceNameConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.api.domain.SysNoticeVO;
import com.ruoyi.system.api.factory.RemoteNoticeFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * 通知公告远程服务接口
 * 仅供 txwx-social-dashboard 等模块通过 Feign 调用
 * 
 * @author txwx
 */
@FeignClient(
        contextId = "remoteNoticeService",
        value = ServiceNameConstants.SYSTEM_SERVICE,
        fallbackFactory = RemoteNoticeFallbackFactory.class
)
public interface RemoteNoticeService {

    /**
     * 插入系统同步通知（类型3）
     *
     * @param vo       通知VO
     * @param source   来源标识
     * @return 结果
     */
    @PostMapping("/internal/v1/notice/system")
    R<Integer> insertSystemNotice(@RequestBody SysNoticeVO vo,
                                   @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 插入用户操作通知（类型4）
     *
     * @param vo       通知VO
     * @param source   来源标识
     * @return 结果
     */
    @PostMapping("/internal/v1/notice/user-oper")
    R<Integer> insertUserOperNotice(@RequestBody SysNoticeVO vo,
                                    @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
