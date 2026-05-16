/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the MIT License;
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.txwx.webchatcrm.controller;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.txwx.webchatcrm.domain.po.TxwxArticlePO;
import com.txwx.webchatcrm.domain.vo.ArticleVO;
import com.txwx.webchatcrm.domain.vo.ArticleDetailVO;
import com.txwx.webchatcrm.domain.vo.PublishStatusVO;
import com.txwx.webchatcrm.domain.vo.PublishedArticleListVO;
import com.txwx.webchatcrm.domain.vo.DraftListVO;
import com.txwx.webchatcrm.service.IArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 微信公众号文章Controller
 *
 * @author txwx
 * @date 2025-01-13
 */
@RestController
@RequestMapping("/article")
public class ArticleController extends BaseController {

    @Autowired
    private IArticleService articleService;

    /**
     * @description: 新增草稿
     */
    @PostMapping("/addDraft")
    public AjaxResult addDraft(@RequestBody ArticleVO articleVO) {
        try {
            articleService.addDraft(articleVO);
            return success("新增草稿成功");
        } catch (Exception e) {
            return error("新增草稿失败: " + e.getMessage());
        }
    }

    /**
     * @description: 查询草稿列表
     */
    @GetMapping("/draftList")
    public AjaxResult getDraftList(@RequestParam(required = false) String status,
                                 @RequestParam(required = false) String submitter,
                                 @RequestParam(required = false) String reviewer,
                                 @RequestParam(required = true, defaultValue = "1") Integer pageNum,
                                 @RequestParam(required = true, defaultValue = "10") Integer pageSize) {
        try {
            List<TxwxArticlePO> list = articleService.getDraftList(status, submitter, reviewer, pageNum, pageSize);
            return success(list);
        } catch (Exception e) {
            return error("查询草稿列表失败: " + e.getMessage());
        }
    }

    /**
     * @description: 查询草稿总数
     */
    @GetMapping("/draftCount")
    public AjaxResult getDraftCount(@RequestParam(required = false) String status,
                                    @RequestParam(required = false) String submitter,
                                    @RequestParam(required = false) String reviewer) {
        try {
            int count = articleService.getDraftCount(status, submitter, reviewer);
            return success(count);
        } catch (Exception e) {
            return error("查询草稿总数失败: " + e.getMessage());
        }
    }

    /**
     * @description: 查询草稿详情
     */
    @GetMapping("/draftDetail/{id}")
    public AjaxResult getDraftDetail(@PathVariable Long id) {
        try {
            ArticleDetailVO detail = articleService.getDraftDetail(id);
            return success(detail);
        } catch (Exception e) {
            return error("查询草稿详情失败: " + e.getMessage());
        }
    }

    /**
     * @description: 获取草稿列表（从微信官方查询）
     */
    @GetMapping("/draftListFromTencent")
    public AjaxResult draftListFromTencent(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                         @RequestParam(required = false, defaultValue = "20") Integer pageSize,
                                         @RequestParam(required = false) Integer noContent) {
        try {
            DraftListVO list = articleService.getDraftListFromTencent(pageNum, pageSize, noContent);
            return success(list);
        } catch (Exception e) {
            return error("获取草稿列表失败: " + e.getMessage());
        }
    }

    /**
     * @description: 查询发布状态
     */
    @GetMapping("/publishStatus/{id}")
    public AjaxResult getPublishStatus(@PathVariable Long id) {
        try {
            PublishStatusVO status = articleService.getPublishStatus(id);
            return success(status);
        } catch (Exception e) {
            return error("查询发布状态失败: " + e.getMessage());
        }
    }

    /**
     * @description: 更新草稿
     */
    @PostMapping("/updateDraft")
    public AjaxResult updateDraft(@RequestBody ArticleVO articleVO) {
        try {
            articleService.updateDraft(articleVO.getId(), articleVO);
            return success("更新草稿成功");
        } catch (Exception e) {
            return error("更新草稿失败: " + e.getMessage());
        }
    }

    /**
     * @description: 删除草稿
     */
    @DeleteMapping("/deleteDraft/{id}")
    public AjaxResult deleteDraft(@PathVariable Long id) {
        try {
            articleService.deleteDraft(id);
            return success("删除草稿成功");
        } catch (Exception e) {
            return error("删除草稿失败: " + e.getMessage());
        }
    }

    /**
     * @description: 提交审核
     */
    @PostMapping("/submitForReview/{id}")
    public AjaxResult submitForReview(@PathVariable Long id) {
        try {
            articleService.submitForReview(id);
            return success("提交审核成功");
        } catch (Exception e) {
            return error("提交审核失败: " + e.getMessage());
        }
    }

    /**
     * @description: 审核草稿
     */
    @PostMapping("/reviewDraft")
    public AjaxResult reviewDraft(@RequestParam Long id,
                               @RequestParam String reviewResult) {
        try {
            articleService.reviewDraft(id, reviewResult);
            return success("审核草稿成功");
        } catch (Exception e) {
            return error("审核草稿失败: " + e.getMessage());
        }
    }

    /**
     * @description: 发布草稿
     */
    @PostMapping("/publishDraft")
    public AjaxResult publishDraft(@RequestParam Long id) {
        try {
            articleService.publishDraft(id);
            return success("发布草稿成功");
        } catch (Exception e) {
            return error("发布草稿失败: " + e.getMessage());
        }
    }

    /**
     * @description: 查询已发布文章列表
     */
    @GetMapping("/publishedList")
    public AjaxResult getPublishedList(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                     @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        try {
            List<TxwxArticlePO> list = articleService.getPublishedList(pageNum, pageSize);
            return success(list);
        } catch (Exception e) {
            return error("查询已发布文章列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/publishedListFromTencent")
    public AjaxResult publishedListFromTencent(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                           @RequestParam(required = false, defaultValue = "20") Integer pageSize,
                                           @RequestParam(required = false) Integer noContent) {
        try {
            PublishedArticleListVO list = articleService.getPublishedListFromTencent(pageNum, pageSize, noContent);
            return success(list);
        } catch (Exception e) {
            return error("获取已发布消息列表失败: " + e.getMessage());
        }
    }

    /**
     * @description: 查询已发布文章总数
     */
    @GetMapping("/publishedCount")
    public AjaxResult getPublishedCount() {
        try {
            int count = articleService.getPublishedCount();
            return success(count);
        } catch (Exception e) {
            return error("查询已发布文章总数失败: " + e.getMessage());
        }
    }

    /**
     * @description: 删除已发布文章
     */
    @DeleteMapping("/deletePublishedArticle/{id}")
    public AjaxResult deletePublishedArticle(@PathVariable Long id) {
        try {
            articleService.deletePublishedArticle(id);
            return success("删除已发布文章成功");
        } catch (Exception e) {
            return error("删除已发布文章失败: " + e.getMessage());
        }
    }
}
