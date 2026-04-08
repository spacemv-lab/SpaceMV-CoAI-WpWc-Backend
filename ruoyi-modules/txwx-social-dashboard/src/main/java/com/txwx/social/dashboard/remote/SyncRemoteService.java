package com.txwx.social.dashboard.remote;

import com.ruoyi.common.core.web.domain.AjaxResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * 同步服务远程调用接口
 * 调用 sync-center 服务进行数据同步
 *
 * @author txwx
 * @date 2026-04-06
 */
@FeignClient(name = "txwx-sync-center", path = "/sync/task")
public interface SyncRemoteService {

    /**
     * 执行同步任务
     *
     * @param taskId 任务ID
     * @return 执行结果
     */
    @PostMapping("/execute/{taskId}")
    AjaxResult execute(@PathVariable("taskId") Long taskId);

    /**
     * 查询待执行的任务
     *
     * @return 待执行任务列表
     */
    @GetMapping("/pending")
    AjaxResult pending();
}
