package com.txwx.social.crm.controller;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.txwx.social.crm.domain.po.TxwxArticlePO;
import com.txwx.social.crm.domain.vo.*;
import com.txwx.social.crm.service.IArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 微信公众号文章Controller
 *
 * @author txwx
 * @date 2025-01-13
 */
@RestController
@RequestMapping("/article")
@Tag(name = "08--【CRM】--文章管理")
public class ArticleController extends BaseController {

    @Autowired
    private IArticleService articleService;

    /**
     * @description: 新增草稿
     */
    @PostMapping("/addDraft")
    @Operation(summary = "新增草稿")
    public AjaxResult addDraft(@Parameter(description = "文章信息") @Valid @RequestBody ArticleVO articleVO) {
        articleService.addDraft(articleVO);
        return success("新增草稿成功");
    }

    /**
     * @description: 查询草稿列表
     */
    @GetMapping("/draftList")
    @Operation(summary = "查询草稿列表")
    public AjaxResult getDraftList(@Parameter(description = "草稿查询请求") @Valid @RequestBody ArticleDraftReqVO reqVO) {
        List<TxwxArticlePO> list = articleService.getDraftList(reqVO);
        return success(list);
    }

    /**
     * @description: 查询草稿总数
     */
    @GetMapping("/draftCount")
    @Operation(summary = "查询草稿总数")
    public AjaxResult getDraftCount(@Parameter(description = "草稿查询请求")@Valid @RequestBody ArticleDraftReqVO reqVO) {
        int count = articleService.getDraftCount(reqVO);
        return success(count);
    }

    /**
     * @description: 查询草稿详情
     */
    @GetMapping("/draftDetail/{id}")
    @Operation(summary = "查询草稿详情")
    public AjaxResult getDraftDetail(@Parameter(description = "草稿ID") @PathVariable Long id) {
        ArticleDetailVO detail = articleService.getDraftDetail(id);
        return success(detail);
    }

    /**
     * @description: 获取草稿列表（从微信官方查询）
     */
    @GetMapping("/draftListFromTencent")
    @Operation(summary = "微信官方获取草稿列表")
    public AjaxResult draftListFromTencent(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                         @RequestParam(required = false, defaultValue = "20") Integer pageSize,
                                         @RequestParam(required = false) Integer noContent,
                                           @NotEmpty(message = "账号列表不能为空")@RequestParam("accountIds") List<Long> accountIds) {

        DraftListVO list = articleService.getDraftListFromTencent(pageNum, pageSize, noContent, accountIds);
        return success(list);
    }

    /**
     * @description: 查询发布状态
     */
    @GetMapping("/publishStatus/{id}")
    @Operation(summary = "查询文章发布状态")
    public AjaxResult getPublishStatus(@PathVariable Long id,
                                       @NotBlank(message = "账号不能为空")@RequestParam("accountId") Long accountId) {
        PublishStatusVO status = articleService.getPublishStatus(id, accountId);
        return success(status);
    }

    /**
     * @description: 更新草稿
     */
    @PostMapping("/updateDraft")
    @Operation(summary = "更新草稿")
    public AjaxResult updateDraft(@Valid @RequestBody ArticleVO articleVO) {
        articleService.updateDraft(articleVO.getId(), articleVO);
        return success("更新草稿成功");
    }

    /**
     * @description: 删除草稿
     */
    @DeleteMapping("/{accountId}/deleteDraft/{id}")
    @Operation(summary = "删除草稿")
    public AjaxResult deleteDraft(@PathVariable("id") Long id,
                                  @PathVariable("accountId") Long accountId) {
        articleService.deleteDraft(id, accountId);
        return success("删除草稿成功");
    }

    /**
     * @description: 提交审核
     */
    @PostMapping("/submitForReview/{id}")
    @Operation(summary = "提交审核")
    public AjaxResult submitForReview(@PathVariable Long id) {
        articleService.submitForReview(id);
        return success("提交审核成功");
    }

    /**
     * @description: 审核草稿
     */
    @PostMapping("/reviewDraft")
    @Operation(summary = "审核草稿")
    public AjaxResult reviewDraft(@RequestParam Long id,
                               @RequestParam String reviewResult) {
        articleService.reviewDraft(id, reviewResult);
        return success("审核草稿成功");
    }

    /**
     * @description: 发布草稿
     */
    @PostMapping("/publishDraft")
    @Operation(summary = "发布草稿")
    public AjaxResult publishDraft(@RequestParam Long id,
                                   @NotBlank(message = "账号不能为空")@RequestParam("accountId") Long accountId) {
        articleService.publishDraft(id, accountId);
        return success("发布草稿成功");
    }

    /**
     * @description: 查询已发布文章列表
     */
    @GetMapping("/publishedList")
    @Operation(summary = "查询已发布文章记录")
    public AjaxResult getPublishedList(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                     @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                       @NotEmpty(message = "账号列表不能为空")  @RequestParam("accountIds") List<Long> accountIds) {
        List<TxwxArticlePO> list = articleService.getPublishedList(pageNum, pageSize, accountIds);
        return success(list);
    }

    @GetMapping("/publishedListFromTencent")
    @Operation(summary = "从微信获取发布列表")
    public AjaxResult publishedListFromTencent(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                           @RequestParam(required = false, defaultValue = "20") Integer pageSize,
                                           @RequestParam(required = false) Integer noContent,
                                               @NotEmpty(message = "账号列表不能为空") @RequestParam("accountIds") List<Long> accountIds) {
        PublishedArticleListVO list = articleService.getPublishedListFromTencent(pageNum, pageSize, noContent, accountIds);
        return success(list);
    }

    /**
     * @description: 查询已发布文章总数
     */
    @GetMapping("/publishedCount")
    @Operation(summary = "查询已发布文章总数")
    public AjaxResult getPublishedCount(@NotEmpty(message = "账号列表不能为空") @RequestParam("accountIds") List<Long> accountIds) {
        int count = articleService.getPublishedCount(accountIds);
        return success(count);
    }

    /**
     * @description: 删除已发布文章
     */
    @DeleteMapping("/{accountId}/deletePublishedArticle/{id}")
    @Operation(summary = "删除已发布文章")
    public AjaxResult deletePublishedArticle(@PathVariable("id") Long id,
                                             @PathVariable("accountId") Long accountId) {
        articleService.deletePublishedArticle(id, accountId);
        return success("删除已发布文章成功");
    }
}
