package com.txwx.social.crm.controller.content;

import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.txwx.social.crm.domain.content.ArticlePublish;
import com.txwx.social.crm.domain.content.ContentArticle;
import com.txwx.social.crm.publisher.PublishContext;
import com.txwx.social.crm.publisher.PublishDispatcher;
import com.txwx.social.crm.publisher.TargetCodes;
import com.txwx.social.crm.service.content.IArticlePublishService;
import com.txwx.social.crm.service.content.IContentArticleService;
import com.txwx.social.crm.service.content.IContentPublishService;
import com.ruoyi.common.security.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/content/articles")
@Tag(name = "内容中心-问道文章管理")
@RequiredArgsConstructor
public class ContentArticleController extends BaseController {

    private final IContentArticleService articleService;
    private final IContentPublishService publishService;
    private final IArticlePublishService articlePublishService;
    private final PublishDispatcher publishDispatcher;

    @PreAuthorize("@ss.hasPermi('content:article:list')")
    @GetMapping
    @Operation(summary = "查询问道文章列表")
    public TableDataInfo list(ContentArticle query) {
        startPage();
        List<ContentArticle> list = articleService.selectList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('content:article:list')")
    @PostMapping("/list")
    @Operation(summary = "查询问道文章列表")
    public TableDataInfo listByPost(@RequestBody(required = false) ContentArticle query) {
        query = query == null ? new ContentArticle() : query;
        startBodyPage(query.getPageNum(), query.getPageSize());
        List<ContentArticle> list = articleService.selectList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('content:article:list')")
    @GetMapping("/{id}")
    @Operation(summary = "查询问道文章详情")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(articleService.selectById(id));
    }

    @PreAuthorize("@ss.hasPermi('content:article:add')")
    @PostMapping
    @Operation(summary = "新增问道文章")
    public AjaxResult add(@Valid @RequestBody ContentArticle article) {
        return success(articleService.create(article));
    }

    @PreAuthorize("@ss.hasPermi('content:article:edit')")
    @PutMapping("/{id}")
    @Operation(summary = "修改问道文章")
    public AjaxResult edit(@PathVariable Long id, @RequestBody ContentArticle article) {
        return success(articleService.update(id, article));
    }

    @PreAuthorize("@ss.hasPermi('content:article:delete')")
    @DeleteMapping("/{id}")
    @Operation(summary = "删除问道文章")
    public AjaxResult remove(@PathVariable Long id) {
        // 同步标记所有平台发布记录为已删除
        articlePublishService.markDeletedByArticleId(id, SecurityUtils.getUsername());
        return toAjax(articleService.deleteById(id));
    }

    @PreAuthorize("@ss.hasPermi('content:article:publish')")
    @PostMapping("/{id}/publish/site")
    @Operation(summary = "发布问道文章到站内（已废弃，请使用 publishMulti）")
    @Deprecated
    public AjaxResult publishSite(@PathVariable Long id) {
        return success(publishService.publishToSite(id));
    }

    @PreAuthorize("@ss.hasPermi('content:article:publish')")
    @PostMapping("/{id}/publish/multi")
    @Operation(summary = "多平台发布文章")
    public AjaxResult publishMulti(@PathVariable Long id, @RequestBody List<PublishTargetDTO> targets) {
        return success(publishService.publishToMulti(id, targets));
    }

    @PreAuthorize("@ss.hasPermi('content:article:edit')")
    @PostMapping("/{id}/draft/wechat")
    @Operation(summary = "保存文章到微信公众号草稿箱（不发布）")
    public AjaxResult saveDraftToWeChat(@PathVariable Long id, @RequestBody PublishTargetDTO target) {
        publishService.saveDraftToWeChat(id, target.getAccountId(), target.getStylePreset());
        return success();
    }

    @PreAuthorize("@ss.hasPermi('content:article:publish')")
    @PostMapping("/{id}/retry")
    @Operation(summary = "单平台重试发布")
    public AjaxResult retryPlatform(@PathVariable Long id, @RequestBody PublishTargetDTO target) {
        ContentArticle article = articleService.selectById(id);
        if (article == null) {
            return error("文章不存在");
        }
        PublishContext context = new PublishContext(target.getTargetCode(), target.getAccountId(), target.getStylePreset());
        return success(publishDispatcher.retryPlatform(context, article));
    }

    @PreAuthorize("@ss.hasPermi('content:publish:record')")
    @GetMapping("/{id}/publish-records")
    @Operation(summary = "查询文章在各平台的发布记录")
    public AjaxResult publishRecords(@PathVariable Long id) {
        return success(articlePublishService.selectByArticleId(id));
    }

    @PreAuthorize("@ss.hasPermi('content:publish:record')")
    @GetMapping("/{id}/publish-records/{platform}")
    @Operation(summary = "查询文章在指定平台的发布记录")
    public AjaxResult publishRecordByPlatform(@PathVariable Long id, @PathVariable String platform) {
        return success(articlePublishService.selectByArticleAndPlatform(id, platform));
    }

    @PreAuthorize("@ss.hasPermi('content:article:publish')")
    @PostMapping("/{id}/preview")
    @Operation(summary = "预览文章在指定平台的渲染效果")
    public AjaxResult previewArticle(@PathVariable Long id, @RequestBody PublishTargetDTO target) {
        ContentArticle article = articleService.selectById(id);
        if (article == null) {
            return error("文章不存在");
        }
        String previewHtml = publishDispatcher.preview(target.getTargetCode(), article, target.getStylePreset());
        return success(new java.util.HashMap<String, Object>() {{
            put("previewHtml", previewHtml);
            put("targetCode", target.getTargetCode());
        }});
    }

    @PreAuthorize("@ss.hasPermi('content:publish:record')")
    @GetMapping("/{id}/publish-results")
    @Operation(summary = "查询文章发布结果（旧版）")
    public AjaxResult publishResults(@PathVariable Long id) {
        return success(publishService.selectResultsByArticleId(id));
    }

    @PreAuthorize("@ss.hasPermi('content:publish:record')")
    @GetMapping("/{id}/publish-jobs")
    @Operation(summary = "查询文章发布任务（旧版）")
    public AjaxResult publishJobs(@PathVariable Long id) {
        return success(publishService.selectJobsByArticleId(id));
    }

    private void startBodyPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum == null ? 1 : pageNum, pageSize == null ? 10 : pageSize);
    }
}
