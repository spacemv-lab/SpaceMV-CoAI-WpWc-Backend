/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.controller.content;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.txwx.social.crm.domain.po.DataSourcePO;
import com.txwx.social.crm.service.content.IDataSourceService;
import com.txwx.social.crm.service.content.fetcher.YahooDataSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/content/site/wendao/datasource")
@Tag(name = "内容中心-数据源管理")
@RequiredArgsConstructor
public class DataSourceController extends BaseController {

    private final IDataSourceService dataSourceService;
    private final YahooDataSyncService yahooDataSyncService;

    @SuppressWarnings("unchecked")
    @PreAuthorize("@ss.hasPermi('datasource:list')")
    @GetMapping("/list")
    @Operation(summary = "分页查询数据源列表")
    public TableDataInfo list(@RequestParam Map<String, Object> params) {
        startPage();
        Map<String, Object> result = dataSourceService.pageList(params);
        List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("rows");
        return getDataTable(rows);
    }

    @PreAuthorize("@ss.hasPermi('datasource:list')")
    @GetMapping("/{id}")
    @Operation(summary = "查询数据源详情")
    public AjaxResult detail(@PathVariable String id) {
        return success(dataSourceService.getById(id));
    }

    @PreAuthorize("@ss.hasPermi('datasource:create')")
    @PostMapping
    @Operation(summary = "新增数据源")
    public AjaxResult create(@Valid @RequestBody DataSourcePO dataSource) {
        dataSourceService.create(dataSource);
        return success(dataSource.getId());
    }

    @PreAuthorize("@ss.hasPermi('datasource:edit')")
    @PutMapping("/{id}")
    @Operation(summary = "编辑数据源")
    public AjaxResult update(@PathVariable String id, @Valid @RequestBody DataSourcePO dataSource) {
        dataSourceService.update(id, dataSource);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('datasource:delete')")
    @DeleteMapping("/{id}")
    @Operation(summary = "删除/停用数据源")
    public AjaxResult delete(@PathVariable String id) {
        dataSourceService.delete(id);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('datasource:sync')")
    @PostMapping("/{id}/sync")
    @Operation(summary = "手动触发数据采集")
    public AjaxResult sync(@PathVariable String id) {
        Map<String, Object> result = dataSourceService.sync(id, "manual");
        if (Boolean.TRUE.equals(result.get("success"))) {
            return success(result);
        }
        return error((String) result.get("error"));
    }

    @PreAuthorize("@ss.hasPermi('datasource:import')")
    @PostMapping("/{id}/import")
    @Operation(summary = "CSV导入数据")
    public AjaxResult importCsv(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = dataSourceService.importCsv(id, file);
        if (Boolean.TRUE.equals(result.get("success"))) {
            return success(result);
        }
        return error((String) result.get("error"));
    }

    @PreAuthorize("@ss.hasPermi('datasource:list')")
    @GetMapping("/sync-logs")
    @Operation(summary = "查询采集日志")
    public TableDataInfo syncLogs(
            @RequestParam(required = false) String sourceId) {
        startPage();
        var list = dataSourceService.getSyncLogs(sourceId);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('datasource:sync')")
    @PostMapping("/sync-category/{categoryId}")
    @Operation(summary = "按FRED分类同步数据源")
    public AjaxResult syncCategory(@PathVariable int categoryId) {
        Map<String, Object> result = dataSourceService.syncCategory(categoryId);
        if (Boolean.TRUE.equals(result.get("success"))) {
            return success(result);
        }
        return error((String) result.get("error"));
    }

    @PreAuthorize("@ss.hasPermi('datasource:sync')")
    @PostMapping("/sync-all-categories")
    @Operation(summary = "同步所有FRED分类")
    public AjaxResult syncAllCategories() {
        dataSourceService.syncAllCategoriesAsync();
        return success("同步已启动，请稍后刷新列表查看结果");
    }

    @PreAuthorize("@ss.hasPermi('datasource:sync')")
    @PostMapping("/yahoo/sync-category/{categorySlug}")
    @Operation(summary = "按Yahoo分类同步数据源")
    public AjaxResult syncYahooCategory(@PathVariable String categorySlug) {
        Map<String, Object> result = yahooDataSyncService.syncCategory(categorySlug);
        if (Boolean.TRUE.equals(result.get("success"))) {
            return success(result);
        }
        return error((String) result.get("error"));
    }

    @PreAuthorize("@ss.hasPermi('datasource:sync')")
    @PostMapping("/yahoo/sync-all-categories")
    @Operation(summary = "同步所有Yahoo分类")
    public AjaxResult syncAllYahooCategories() {
        yahooDataSyncService.syncAllCategoriesAsync();
        return success("同步已启动，请稍后刷新列表查看结果");
    }

    @PreAuthorize("@ss.hasPermi('datasource:sync')")
    @PostMapping("/yahoo/sync-all-data")
    @Operation(summary = "同步所有Yahoo数据源的时序数据（兜底）")
    public AjaxResult syncAllYahooData() {
        yahooDataSyncService.syncAllDataAsync();
        return success("Yahoo全量数据同步已启动，请稍后查看结果");
    }
}
