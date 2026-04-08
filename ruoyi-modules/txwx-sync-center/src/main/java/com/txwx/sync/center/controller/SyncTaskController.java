package com.txwx.sync.center.controller;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.txwx.sync.center.domain.po.SyncTaskPO;
import com.txwx.sync.center.service.ISyncTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 同步任务控制器
 *
 * @author txwx
 * @date 2026-04-03
 */
@RestController
@RequestMapping("/sync/task")
@Tag(name = "06--【Sync】--同步任务")
public class SyncTaskController extends BaseController {

    @Autowired
    private ISyncTaskService syncTaskService;

    /**
     * 查询任务列表
     */
    @PostMapping("/list")
    @Operation(summary = "查询任务列表")
    public AjaxResult list(SyncTaskPO task) {
        return AjaxResult.success(syncTaskService.selectTaskList(task));
    }

    /**
     * 查询任务详情
     */
    @GetMapping("/getOne")
    @Operation(summary = "查询任务详情")
    public AjaxResult getOne(Long id) {
        return AjaxResult.success(syncTaskService.selectTaskById(id));
    }

    /**
     * 新增任务
     */
    @PostMapping("/add")
    @Operation(summary = "新增任务")
    public AjaxResult add(@RequestBody SyncTaskPO task) {
        return toAjax(syncTaskService.insertTask(task));
    }

    /**
     * 修改任务
     */
    @PostMapping("/update")
    @Operation(summary = "修改任务")
    public AjaxResult update(@RequestBody SyncTaskPO task) {
        return toAjax(syncTaskService.updateTask(task));
    }

    /**
     * 删除任务
     */
    @DeleteMapping("/delete/{ids}")
    @Operation(summary = "删除任务")
    public AjaxResult delete(@PathVariable Long[] ids) {
        return toAjax(syncTaskService.deleteTaskByIds(ids));
    }

    /**
     * 执行同步任务
     */
    @PostMapping("/execute/{taskId}")
    @Operation(summary = "执行同步任务")
    public AjaxResult execute(@PathVariable Long taskId) {
        String result = syncTaskService.executeTask(taskId);
        return AjaxResult.success(result);
    }

    /**
     * 查询待执行的任务
     */
    @GetMapping("/pending")
    @Operation(summary = "查询待执行的任务")
    public AjaxResult pending() {
        return AjaxResult.success(syncTaskService.selectPendingTasks());
    }
}
