package com.txwx.social.crm.controller.content;

import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.utils.JwtUtils;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.txwx.social.crm.domain.content.ContentArticle;
import com.txwx.social.crm.domain.content.TagCount;
import com.txwx.social.crm.service.content.IContentArticleService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/content/site/wendao/articles")
@Tag(name = "内容中心-问道站点文章")
@RequiredArgsConstructor
public class ContentWendaoSiteArticleController extends BaseController {

    private final IContentArticleService articleService;

    @GetMapping
    @Operation(summary = "查询问道站点文章列表")
    public TableDataInfo list(ContentArticle query) {
        startPage();
        List<ContentArticle> list = articleService.selectWendaoSiteList(query);
        return getDataTable(list);
    }

    @PostMapping("/list")
    @Operation(summary = "查询问道站点文章列表")
    public TableDataInfo listByPost(@RequestBody(required = false) ContentArticle query) {
        query = query == null ? new ContentArticle() : query;
        startBodyPage(query.getPageNum(), query.getPageSize());
        List<ContentArticle> list = articleService.selectWendaoSiteList(query);
        return getDataTable(list);
    }

    @GetMapping("/tags")
    @Operation(summary = "获取文章标签列表（含文章数）")
    public AjaxResult listTags() {
        List<TagCount> tags = articleService.selectWendaoSiteTags();
        return success(tags);
    }

    @GetMapping("/hot")
    @Operation(summary = "获取热门文章列表（最新发布）")
    public AjaxResult listHot(@RequestParam(defaultValue = "5") int limit) {
        List<ContentArticle> list = articleService.selectWendaoSiteHotList(limit);
        return success(list);
    }

    @GetMapping("/{slug}")
    @Operation(summary = "查询问道站点文章详情")
    public AjaxResult getBySlug(@PathVariable String slug, HttpServletRequest request) {
        boolean authenticated = isAuthenticated(request);
        String plan = getPlanFromToken(request);
        return success(articleService.selectWendaoSiteBySlug(slug, authenticated, plan));
    }

    private boolean isAuthenticated(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }
        try {
            return JwtUtils.parseToken(authHeader.substring(7)) != null;
        } catch (Exception e) {
            return false;
        }
    }

    private String getPlanFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return "";
        }
        try {
            Claims claims = JwtUtils.parseToken(authHeader.substring(7));
            String plan = claims.get("plan", String.class);
            return plan != null ? plan : "";
        } catch (Exception e) {
            return "";
        }
    }

    private void startBodyPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum == null ? 1 : pageNum, pageSize == null ? 10 : pageSize);
    }
}
