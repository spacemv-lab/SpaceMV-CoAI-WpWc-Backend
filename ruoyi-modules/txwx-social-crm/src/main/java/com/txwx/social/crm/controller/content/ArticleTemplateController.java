package com.txwx.social.crm.controller.content;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.txwx.social.crm.domain.content.ArticleTemplate;
import com.txwx.social.crm.service.content.IArticleTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 文章模板 Controller。
 */
@RestController
@RequestMapping("/content/templates")
@Tag(name = "内容中心-文章模板管理")
@RequiredArgsConstructor
public class ArticleTemplateController extends BaseController {

    private final IArticleTemplateService templateService;

    @PreAuthorize("@ss.hasPermi('content:template:list')")
    @GetMapping
    @Operation(summary = "查询模板列表")
    public TableDataInfo list(ArticleTemplate query) {
        startPage();
        List<ArticleTemplate> list = templateService.selectList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('content:template:list')")
    @GetMapping("/{id}")
    @Operation(summary = "查询模板详情")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(templateService.selectById(id));
    }

    @PreAuthorize("@ss.hasPermi('content:template:list')")
    @GetMapping("/platform/{platform}")
    @Operation(summary = "按平台查询模板")
    public AjaxResult getByPlatform(@PathVariable String platform) {
        return success(templateService.selectByPlatform(platform));
    }

    @PreAuthorize("@ss.hasPermi('content:template:list')")
    @GetMapping("/default")
    @Operation(summary = "查询默认模板")
    public AjaxResult getDefault() {
        return success(templateService.selectDefault());
    }

    @PreAuthorize("@ss.hasPermi('content:template:add')")
    @PostMapping
    @Operation(summary = "新增模板")
    public AjaxResult add(@Valid @RequestBody ArticleTemplate template) {
        return success(templateService.create(template));
    }

    @PreAuthorize("@ss.hasPermi('content:template:edit')")
    @PutMapping("/{id}")
    @Operation(summary = "修改模板")
    public AjaxResult edit(@PathVariable Long id, @RequestBody ArticleTemplate template) {
        template.setId(id);
        templateService.update(template);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('content:template:delete')")
    @DeleteMapping("/{id}")
    @Operation(summary = "删除模板")
    public AjaxResult remove(@PathVariable Long id) {
        templateService.deleteById(id);
        return success();
    }
}
