package com.txwx.sync.center.controller;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.txwx.sync.center.domain.po.SyncLogPO;
import com.txwx.sync.center.service.ISyncLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 同步状态控制器
 *
 * @author txwx
 * @date 2026-04-03
 */
@RestController
@RequestMapping("/sync/status")
@Tag(name = "07--【Sync】--同步状态")
public class SyncStatusController extends BaseController {

    @Autowired
    private ISyncLogService syncLogService;

    /**
     * 查询日志列表
     */
    @PostMapping("/list")
    @Operation(summary = "查询日志列表")
    public AjaxResult list(SyncLogPO log) {
        return AjaxResult.success(syncLogService.selectLogList(log));
    }

    /**
     * 查询日志详情
     */
    @GetMapping("/getOne")
    @Operation(summary = "查询日志详情")
    public AjaxResult getOne(Long id) {
        return AjaxResult.success(syncLogService.selectLogById(id));
    }

    /**
     * 查询最新的同步状态
     */
    @GetMapping("/latest/{accountId}")
    @Operation(summary = "查询最新的同步状态")
    public AjaxResult latest(@PathVariable Long accountId) {
        return AjaxResult.success(syncLogService.selectLatestSyncStatus(accountId));
    }
}
