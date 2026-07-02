package com.txwx.social.crm.controller.content;

import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.txwx.social.crm.domain.content.ContentArticle;
import com.txwx.social.crm.service.content.IContentArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/content/public/articles")
@Tag(name = "内容中心-公开文章")
@RequiredArgsConstructor
public class ContentPublicArticleController extends BaseController {

    private final IContentArticleService articleService;

    @GetMapping
    @Operation(summary = "查询公开文章列表")
    public TableDataInfo list(ContentArticle query) {
        startPage();
        List<ContentArticle> list = articleService.selectPublicList(query);
        return getDataTable(list);
    }

    @PostMapping("/list")
    @Operation(summary = "查询公开文章列表")
    public TableDataInfo listByPost(@RequestBody(required = false) ContentArticle query) {
        query = query == null ? new ContentArticle() : query;
        startBodyPage(query.getPageNum(), query.getPageSize());
        List<ContentArticle> list = articleService.selectPublicList(query);
        return getDataTable(list);
    }

    @GetMapping("/{slug}")
    @Operation(summary = "查询公开文章详情")
    public AjaxResult getBySlug(@PathVariable String slug) {
        return success(articleService.selectPublicBySlug(slug));
    }

    private void startBodyPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum == null ? 1 : pageNum, pageSize == null ? 10 : pageSize);
    }
}
