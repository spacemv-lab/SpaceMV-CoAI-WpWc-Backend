package com.txwx.social.crm.controller.content;

import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.txwx.social.crm.domain.content.ContentPublishJob;
import com.txwx.social.crm.domain.content.ContentPublishResult;
import com.txwx.social.crm.domain.po.TxwxAccountPO;
import com.txwx.social.crm.service.IAccountService;
import com.txwx.social.crm.service.content.IContentPublishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/content/publish")
@Tag(name = "内容中心-发布记录")
@RequiredArgsConstructor
public class ContentPublishController extends BaseController {

    private final IContentPublishService publishService;
    private final IAccountService accountService;

    @PreAuthorize("@ss.hasPermi('content:publish:record')")
    @GetMapping("/jobs")
    @Operation(summary = "查询发布任务列表")
    public TableDataInfo jobs(ContentPublishJob query) {
        startPage();
        List<ContentPublishJob> list = publishService.selectJobList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('content:publish:record')")
    @PostMapping("/jobs/list")
    @Operation(summary = "查询发布任务列表")
    public TableDataInfo jobsByPost(@RequestBody(required = false) ContentPublishJob query) {
        query = query == null ? new ContentPublishJob() : query;
        startBodyPage(query.getPageNum(), query.getPageSize());
        List<ContentPublishJob> list = publishService.selectJobList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('content:publish:record')")
    @GetMapping("/jobs/{id}")
    @Operation(summary = "查询发布任务详情")
    public AjaxResult jobInfo(@PathVariable Long id) {
        return success(publishService.selectJobById(id));
    }

    @PreAuthorize("@ss.hasPermi('content:publish:record')")
    @GetMapping("/results")
    @Operation(summary = "查询发布结果列表")
    public TableDataInfo results(ContentPublishResult query) {
        startPage();
        List<ContentPublishResult> list = publishService.selectResultList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('content:publish:record')")
    @PostMapping("/results/list")
    @Operation(summary = "查询发布结果列表")
    public TableDataInfo resultsByPost(@RequestBody(required = false) ContentPublishResult query) {
        query = query == null ? new ContentPublishResult() : query;
        startBodyPage(query.getPageNum(), query.getPageSize());
        List<ContentPublishResult> list = publishService.selectResultList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('content:publish:record')")
    @GetMapping("/wechat/accounts")
    @Operation(summary = "获取微信公众号账号列表")
    public AjaxResult wechatAccounts() {
        TxwxAccountPO query = new TxwxAccountPO();
        query.setStatus("1");
        List<TxwxAccountPO> accounts = accountService.selectAccountList(query);
        return success(accounts);
    }

    private void startBodyPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum == null ? 1 : pageNum, pageSize == null ? 10 : pageSize);
    }
}
