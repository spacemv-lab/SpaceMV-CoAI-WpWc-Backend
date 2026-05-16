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
 
 * the copywrite is belongs to  SpaceMV  team
 */
package com.txwx.webchatcrm.service.Impl;

import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.webchatcrm.domain.po.TxwxArticlePO;
import com.txwx.webchatcrm.domain.vo.*;
import com.txwx.webchatcrm.dto.*;
import com.txwx.webchatcrm.enums.ArticleStatusEnum;
import com.txwx.webchatcrm.mapper.TxwxArticleMapper;
import com.txwx.webchatcrm.service.IArticleService;
import com.txwx.webchatcrm.util.Base64Util;
import com.txwx.webchatcrm.util.WebChatUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 微信公众号文章服务实现类
 *
 * @author txwx
 * @date 2025-01-13
 */
@Service
public class ArticleServiceImpl implements IArticleService {

    private static final Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Autowired
    private TxwxArticleMapper txwxArticleMapper;

    @Value("${weixin.appId}")
    private String appId;

    @Value("${weixin.secret}")
    private String secret;

    @Override
    @Transactional
    public void addDraft(ArticleVO articleVO) {
        try {
            // 1. 调用微信API新增草稿
            String accessToken = WebChatUtil.getAccessToken(appId, secret);

            ArticleItemDTO articleItem = new ArticleItemDTO();
            articleItem.setArticle_type(articleVO.getArticleType() != null ? articleVO.getArticleType() : "news");
            articleItem.setTitle(articleVO.getTitle());
            articleItem.setAuthor(articleVO.getAuthor());
            articleItem.setDigest(articleVO.getDigest());
            // 对content字段进行Base64解码
            String content = Base64Util.isBase64(articleVO.getContent())
                ? Base64Util.decode(articleVO.getContent())
                : articleVO.getContent();
            articleItem.setContent(content);
            articleItem.setThumb_media_id(articleVO.getThumbMediaId());
            articleItem.setNeed_open_comment(articleVO.getNeedOpenComment() != null ? articleVO.getNeedOpenComment() : 0);
            articleItem.setOnly_fans_can_comment(articleVO.getOnlyFansCanComment() != null ? articleVO.getOnlyFansCanComment() : 0);

            ArticleDTO articleDTO = new ArticleDTO();
            articleDTO.setArticles(new ArrayList<>());
            articleDTO.getArticles().add(articleItem);

            AddDraftResponse response = WebChatUtil.addDraft(accessToken, articleDTO);

            /**
             * @description: 以当前登录用户作为提交人
             */
            String username = SecurityUtils.getLoginUser().getUsername();
            Long userid = SecurityUtils.getLoginUser().getUserid();
            //Long userid = 1l;
            //String username = "admin";

            // 2. 保存到数据库
            TxwxArticlePO article = new TxwxArticlePO();
            article.setMediaId(response.getMedia_id());
            article.setTitle(articleVO.getTitle());
            article.setAuthor(articleVO.getAuthor());
            article.setDigest(articleVO.getDigest());
            article.setContent(content);
            article.setThumbMediaId(articleVO.getThumbMediaId());
            article.setNeedOpenComment(articleVO.getNeedOpenComment() != null ? articleVO.getNeedOpenComment() : 0);
            article.setOnlyFansCanComment(articleVO.getOnlyFansCanComment() != null ? articleVO.getOnlyFansCanComment() : 0);
            article.setArticleType(articleVO.getArticleType() != null ? articleVO.getArticleType() : "news");
            article.setStatus(ArticleStatusEnum.PENDING_SUBMIT.getCode());
            article.setSubmitter(username);
            article.setSubmitterId(userid);
            article.setCreateTime(new Date());
            article.setUpdateTime(new Date());

            txwxArticleMapper.insertArticle(article);
        } catch (Exception e) {
            throw new RuntimeException("新增草稿失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TxwxArticlePO> getDraftList(String status, String submitter, String reviewer, Integer pageNum, Integer pageSize) {
        try {
            // 计算分页偏移量
            int offset = (pageNum - 1) * pageSize;
            return txwxArticleMapper.selectDraftList(status, submitter, reviewer, offset, pageSize);
        } catch (Exception e) {
            throw new RuntimeException("查询草稿列表失败: " + e.getMessage(), e);
        }
    }

    @Override
    public int getDraftCount(String status, String submitter, String reviewer) {
        try {
            return txwxArticleMapper.selectDraftCount(status, submitter, reviewer);
        } catch (Exception e) {
            throw new RuntimeException("查询草稿总数失败: " + e.getMessage(), e);
        }
    }

    @Override
    public ArticleDetailVO getDraftDetail(Long id) {
        try {
            // 1. 从数据库查询文章，获取mediaId
            TxwxArticlePO article = txwxArticleMapper.selectArticleById(id);
            if (article == null) {
                throw new RuntimeException("草稿不存在");
            }

            // 2. 调用微信API获取草稿详情
            String accessToken = WebChatUtil.getAccessToken(appId, secret);
            GetDraftDetailResponse response = WebChatUtil.getDraftDetail(accessToken, article.getMediaId());

            // 3. 转换为VO对象
            if (response != null && response.getNews_item() != null && !response.getNews_item().isEmpty()) {
                GetDraftDetailResponse.ArticleDetailItem item = response.getNews_item().get(0);
                com.txwx.webchatcrm.domain.vo.ArticleDetailVO detailVO = new com.txwx.webchatcrm.domain.vo.ArticleDetailVO();
                detailVO.setArticleType(item.getArticle_type());
                detailVO.setTitle(item.getTitle());
                detailVO.setAuthor(item.getAuthor());
                detailVO.setDigest(item.getDigest());
                if(StringUtils.isNotEmpty(item.getContent())){
                    detailVO.setContent(item.getContent().replace("data-src", "src"));
                }
                detailVO.setContentSourceUrl(item.getContent_source_url());
                detailVO.setShowCoverPic(item.getShow_cover_pic());
                detailVO.setThumbMediaId(item.getThumb_media_id());
                detailVO.setThumbUrl(item.getThumb_url());
                detailVO.setUrl(item.getUrl());
                detailVO.setNeedOpenComment(item.getNeed_open_comment());
                detailVO.setOnlyFansCanComment(item.getOnly_fans_can_comment());
                return detailVO;
            }

            throw new RuntimeException("获取草稿详情失败: 未返回有效数据");
        } catch (Exception e) {
            throw new RuntimeException("查询草稿详情失败: " + e.getMessage(), e);
        }
    }

    @Override
    public DraftListVO getDraftListFromTencent(Integer pageNum, Integer pageSize, Integer noContent) {
        try {
            // 将pageNum和pageSize转换为offset和count
            int offset = (pageNum - 1) * pageSize;
            int count = pageSize;

            // 调用微信API获取草稿列表
            String accessToken = WebChatUtil.getAccessToken(appId, secret);
            GetDraftListResponse response = WebChatUtil.getDraftList(accessToken, offset, count, noContent);

            // 转换为VO对象
            DraftListVO listVO = new DraftListVO();
            listVO.setTotalCount(response.getTotal_count());
            listVO.setItemCount(response.getItem_count());

            if (response.getItem() != null && !response.getItem().isEmpty()) {
                List<DraftListVO.DraftItem> items = new ArrayList<>();
                for (GetDraftListResponse.DraftItem item : response.getItem()) {
                    DraftListVO.DraftItem voItem = new DraftListVO.DraftItem();
                    voItem.setMediaId(item.getMedia_id());
                    voItem.setUpdateTime(item.getUpdate_time());

                    // 处理news_item列表
                    if (item.getContent() != null && item.getContent().getNews_item() != null) {
                        List<DraftListVO.NewsItem> newsItems = new ArrayList<>();
                        for (GetDraftListResponse.NewsItem newsItem : item.getContent().getNews_item()) {
                            DraftListVO.NewsItem voNewsItem = new DraftListVO.NewsItem();
                            voNewsItem.setArticleType(newsItem.getArticle_type());
                            voNewsItem.setTitle(newsItem.getTitle());
                            voNewsItem.setAuthor(newsItem.getAuthor());
                            voNewsItem.setDigest(newsItem.getDigest());
                            voNewsItem.setContent(newsItem.getContent());
                            voNewsItem.setContentSourceUrl(newsItem.getContent_source_url());
                            voNewsItem.setShowCoverPic(newsItem.getShow_cover_pic());
                            voNewsItem.setThumbMediaId(newsItem.getThumb_media_id());
                            voNewsItem.setThumbUrl(newsItem.getThumb_url());
                            voNewsItem.setNeedOpenComment(newsItem.getNeed_open_comment());
                            voNewsItem.setOnlyFansCanComment(newsItem.getOnly_fans_can_comment());
                            voNewsItem.setUrl(newsItem.getUrl());
                            newsItems.add(voNewsItem);
                        }
                        voItem.setNewsItems(newsItems);
                    }
                    items.add(voItem);
                }
                listVO.setItems(items);
            }

            return listVO;
        } catch (Exception e) {
            throw new RuntimeException("获取草稿列表失败: " + e.getMessage(), e);
        }
    }

    @Override
    public PublishStatusVO getPublishStatus(Long id) {
        try {
            // 1. 从数据库查询文章，获取publishId
            TxwxArticlePO article = txwxArticleMapper.selectArticleById(id);
            if (article == null) {
                throw new RuntimeException("文章不存在");
            }

            if (StringUtils.isEmpty(article.getPublishId())) {
                throw new RuntimeException("该文章尚未发布");
            }

            // 2. 调用微信API查询发布状态
            String accessToken = WebChatUtil.getAccessToken(appId, secret);
            GetPublishStatusResponse response = WebChatUtil.getPublishStatus(accessToken, article.getPublishId());

            // 3. 转换为VO对象
            PublishStatusVO statusVO = new PublishStatusVO();
            statusVO.setPublishId(response.getPublish_id());
            statusVO.setPublishStatus(response.getPublish_status());
            statusVO.setPublishStatusDesc(getPublishStatusDesc(response.getPublish_status()));
            statusVO.setArticleId(response.getArticle_id());
            statusVO.setFailIdx(response.getFail_idx());

            // 4. 处理文章详情
            if (response.getArticle_detail() != null) {
                GetPublishStatusResponse.ArticleDetail articleDetail = response.getArticle_detail();
                statusVO.setCount(articleDetail.getCount());

                if (articleDetail.getItem() != null && !articleDetail.getItem().isEmpty()) {
                    List<PublishStatusVO.ArticleItem> items = new ArrayList<>();
                    for (GetPublishStatusResponse.ArticleItem item : articleDetail.getItem()) {
                        PublishStatusVO.ArticleItem voItem = new PublishStatusVO.ArticleItem();
                        voItem.setIdx(item.getIdx());
                        voItem.setArticleUrl(item.getArticle_url());
                        items.add(voItem);
                    }
                    statusVO.setArticleItems(items);
                }
            }

            return statusVO;
        } catch (Exception e) {
            throw new RuntimeException("查询发布状态失败: " + e.getMessage(), e);
        }
    }

    /**
     * @description: 根据发布状态码获取描述
     */
    private String getPublishStatusDesc(Integer status) {
        if (status == null) {
            return "未知状态";
        }
        switch (status) {
            case 0:
                return "成功";
            case 1:
                return "发布中";
            case 2:
                return "原创失败";
            case 3:
                return "常规失败";
            case 4:
                return "平台审核不通过";
            case 5:
                return "成功后用户删除所有文章";
            case 6:
                return "成功后系统封禁所有文章";
            default:
                return "未知状态";
        }
    }

    @Override
    public PublishedArticleListVO getPublishedListFromTencent(Integer pageNum, Integer pageSize, Integer noContent) {
        try {
            // 将pageNum和pageSize转换为offset和count
            int offset = (pageNum - 1) * pageSize;
            int count = pageSize;

            // 调用微信API获取已发布消息列表
            String accessToken = WebChatUtil.getAccessToken(appId, secret);
            GetPublishedListResponse response = WebChatUtil.getPublishedList(accessToken, offset, count, noContent);

            // 转换为VO对象
            PublishedArticleListVO listVO = new PublishedArticleListVO();
            listVO.setTotalCount(response.getTotal_count());
            listVO.setItemCount(response.getItem_count());

            if (response.getItem() != null && !response.getItem().isEmpty()) {
                List<PublishedArticleListVO.PublishedArticleItem> items = new ArrayList<>();
                for (GetPublishedListResponse.PublishedItem item : response.getItem()) {
                    PublishedArticleListVO.PublishedArticleItem voItem = new PublishedArticleListVO.PublishedArticleItem();
                    voItem.setArticleId(item.getArticle_id());
                    voItem.setUpdateTime(item.getUpdate_time());

                    // 处理news_item列表
                    if (item.getContent() != null && item.getContent().getNews_item() != null) {
                        List<PublishedArticleListVO.NewsItem> newsItems = new ArrayList<>();
                        for (GetPublishedListResponse.NewsItem newsItem : item.getContent().getNews_item()) {
                            PublishedArticleListVO.NewsItem voNewsItem = new PublishedArticleListVO.NewsItem();
                            voNewsItem.setTitle(newsItem.getTitle());
                            voNewsItem.setAuthor(newsItem.getAuthor());
                            voNewsItem.setDigest(newsItem.getDigest());
                            voNewsItem.setContent(newsItem.getContent());
                            voNewsItem.setContentSourceUrl(newsItem.getContent_source_url());
                            voNewsItem.setThumbMediaId(newsItem.getThumb_media_id());
                            voNewsItem.setThumbUrl(newsItem.getThumb_url());
                            voNewsItem.setNeedOpenComment(newsItem.getNeed_open_comment());
                            voNewsItem.setOnlyFansCanComment(newsItem.getOnly_fans_can_comment());
                            voNewsItem.setUrl(newsItem.getUrl());
                            voNewsItem.setIsDeleted(newsItem.getIs_deleted());
                            newsItems.add(voNewsItem);
                        }
                        voItem.setNewsItems(newsItems);
                    }
                    items.add(voItem);
                }
                listVO.setItems(items);
            }

            return listVO;
        } catch (Exception e) {
            throw new RuntimeException("获取已发布消息列表失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void updateDraft(Long id, ArticleVO articleVO) {
        try {
            // 1. 查询原文章
            TxwxArticlePO existingArticle = txwxArticleMapper.selectArticleById(id);
            if (existingArticle == null) {
                throw new RuntimeException("草稿不存在");
            }

            // 2. 更新微信草稿
            String accessToken = WebChatUtil.getAccessToken(appId, secret);

            ArticleItemDTO articleItem = new ArticleItemDTO();
            articleItem.setArticle_type(articleVO.getArticleType() != null ? articleVO.getArticleType() : "news");
            articleItem.setTitle(articleVO.getTitle());
            articleItem.setAuthor(articleVO.getAuthor());
            articleItem.setDigest(articleVO.getDigest());
            // 对content字段进行Base64解码
            String content = Base64Util.isBase64(articleVO.getContent())
                ? Base64Util.decode(articleVO.getContent())
                : articleVO.getContent();
            articleItem.setContent(content);
            articleItem.setThumb_media_id(articleVO.getThumbMediaId());
            articleItem.setNeed_open_comment(articleVO.getNeedOpenComment() != null ? articleVO.getNeedOpenComment() : 0);
            articleItem.setOnly_fans_can_comment(articleVO.getOnlyFansCanComment() != null ? articleVO.getOnlyFansCanComment() : 0);

            ArticleUpdateDTO articleDTO = new ArticleUpdateDTO();
            articleDTO.setMedia_id(existingArticle.getMediaId());
            articleDTO.setIndex(articleVO.getIndex());
            articleDTO.setArticles(articleItem);

            WebChatUtil.updateDraft(accessToken, articleDTO);

            // 3. 更新数据库
            existingArticle.setTitle(articleVO.getTitle());
            existingArticle.setAuthor(articleVO.getAuthor());
            existingArticle.setDigest(articleVO.getDigest());
            existingArticle.setContent(content);
            existingArticle.setThumbMediaId(articleVO.getThumbMediaId());
            existingArticle.setNeedOpenComment(articleVO.getNeedOpenComment() != null ? articleVO.getNeedOpenComment() : 0);
            existingArticle.setOnlyFansCanComment(articleVO.getOnlyFansCanComment() != null ? articleVO.getOnlyFansCanComment() : 0);
            existingArticle.setArticleType(articleVO.getArticleType() != null ? articleVO.getArticleType() : "news");
            existingArticle.setStatus(ArticleStatusEnum.PENDING_SUBMIT.getCode());
            existingArticle.setUpdateTime(new Date());

            txwxArticleMapper.updateArticle(existingArticle);
        } catch (Exception e) {
            throw new RuntimeException("更新草稿失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteDraft(Long id) {
        try {
            // 1. 查询文章
            TxwxArticlePO article = txwxArticleMapper.selectArticleById(id);
            if (article == null) {
                throw new RuntimeException("草稿不存在");
            }

            // 2. 调用微信API删除草稿
            if (StringUtils.isNotEmpty(article.getMediaId())) {
                String accessToken = WebChatUtil.getAccessToken(appId, secret);
                WebChatUtil.deleteDraft(accessToken, article.getMediaId());
            }

            // 3. 删除数据库记录
            txwxArticleMapper.deleteArticle(id);
        } catch (Exception e) {
            throw new RuntimeException("删除草稿失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void submitForReview(Long id) {
        try {
            // 1. 查询文章
            TxwxArticlePO article = txwxArticleMapper.selectArticleById(id);
            if (article == null) {
                throw new RuntimeException("草稿不存在");
            }

            // 2. 更新状态为待审核
            article.setStatus(ArticleStatusEnum.PENDING_REVIEW.getCode());
            article.setUpdateTime(new Date());

            txwxArticleMapper.updateArticle(article);
        } catch (Exception e) {
            throw new RuntimeException("提交审核失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void reviewDraft(Long id, String reviewResult) {
        try {
            // 1. 查询文章
            TxwxArticlePO article = txwxArticleMapper.selectArticleById(id);
            if (article == null) {
                throw new RuntimeException("草稿不存在");
            }

            /**
             * @description: 以当前登录用户作为审核人
             */
            String username = SecurityUtils.getLoginUser().getUsername();
            //String username = "admin";

            // 2. 更新状态
            if ("pass".equalsIgnoreCase(reviewResult)) {
                article.setStatus(ArticleStatusEnum.REVIEW_APPROVED.getCode());
            } else {
                article.setStatus(ArticleStatusEnum.REVIEW_REJECTED.getCode());
            }
            article.setReviewer(username);
            article.setUpdateTime(new Date());

            txwxArticleMapper.updateArticle(article);
        } catch (Exception e) {
            throw new RuntimeException("审核草稿失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void publishDraft(Long id) {
        try {
            // 1. 查询文章
            TxwxArticlePO article = txwxArticleMapper.selectArticleById(id);
            if (article == null) {
                throw new RuntimeException("草稿不存在");
            }

            // 2. 调用微信API发布
            String accessToken = WebChatUtil.getAccessToken(appId, secret);
            PublishDraftResponse response = WebChatUtil.publishDraft(accessToken, article.getMediaId());

            // 3. 转换为VO对象
            /**
             * @description: 以当前登录用户作为发布人
             */
            String username = SecurityUtils.getLoginUser().getUsername();
            //String username = "admin";

            // 3. 更新数据库
            article.setPublishId(response.getPublish_id());
            article.setMsgDataId(response.getMsg_data_id());
            article.setStatus(ArticleStatusEnum.PUBLISHING.getCode());
            article.setPublisher(username);
            article.setUpdateTime(new Date());

            txwxArticleMapper.updateArticle(article);
        } catch (Exception e) {
            throw new RuntimeException("发布草稿失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TxwxArticlePO> getPublishedList(Integer pageNum, Integer pageSize) {
        try {
            // 计算分页偏移量
            int offset = (pageNum - 1) * pageSize;
            return txwxArticleMapper.selectPublishedList(offset, pageSize);
        } catch (Exception e) {
            throw new RuntimeException("查询已发布文章列表失败: " + e.getMessage(), e);
        }
    }

    @Override
    public int getPublishedCount() {
        try {
            return txwxArticleMapper.selectPublishedCount();
        } catch (Exception e) {
            throw new RuntimeException("查询已发布文章总数失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deletePublishedArticle(Long id) {
        try {
            // 1. 查询文章
            TxwxArticlePO article = txwxArticleMapper.selectArticleById(id);
            if (article == null) {
                throw new RuntimeException("文章不存在");
            }

            // 2. 调用微信API删除已发布文章
            if(StringUtils.isNotEmpty(article.getPublishId())){
                String accessToken = WebChatUtil.getAccessToken(appId, secret);
                GetPublishStatusResponse response = WebChatUtil.getPublishStatus(accessToken, article.getPublishId());

                if(response.getPublish_status() == 0){
                    if (response.getArticle_detail() != null) {
                        String article_id = response.getArticle_id();
                        GetPublishStatusResponse.ArticleDetail articleDetail = response.getArticle_detail();

                        if (articleDetail.getItem() != null && !articleDetail.getItem().isEmpty()) {
                            GetPublishStatusResponse.ArticleItem item = articleDetail.getItem().get(0);

                            Integer idx = item.getIdx();
                            WebChatUtil.deletePublishedArticle(accessToken, idx, article_id);
                        }else{
                            throw new RuntimeException("微信官网文章详情报文体为空");
                        }
                    }else{
                        throw new RuntimeException("微信官网文章报文体为空");
                    }
                }else{
                    throw new RuntimeException("Publish_status:" + response.getPublish_status());
                }
            }else{
                throw new RuntimeException("PublishId为空");
            }

            // 3. 删除数据库记录
            txwxArticleMapper.deleteArticle(id);
        } catch (Exception e) {
            throw new RuntimeException("删除已发布文章失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void updatePublishingArticleStatus() {
        try {
            // 1. 查询所有发布中的文章
            List<TxwxArticlePO> publishingArticles = txwxArticleMapper.selectPublishingArticles();
            if (publishingArticles == null || publishingArticles.isEmpty()) {
                return;
            }

            logger.info("需要更新发布文章状态的数量:" + publishingArticles.size());
            // 2. 准备批量更新的文章列表
            List<TxwxArticlePO> articlesToUpdate = new ArrayList<>();
            String accessToken = WebChatUtil.getAccessToken(appId, secret);

            // 3. 遍历每篇文章，查询发布状态
            for (TxwxArticlePO article : publishingArticles) {
                try {
                    if (StringUtils.isEmpty(article.getPublishId())) {
                        continue;
                    }

                    // 调用微信API查询发布状态
                    GetPublishStatusResponse response = WebChatUtil.getPublishStatus(accessToken, article.getPublishId());

                    if (response != null && response.getPublish_status() != null) {
                        // 转换微信状态为本地状态
                        String newStatus = convertPublishStatusToArticleStatus(response.getPublish_status());

                        // 只有状态不是"发布中"才更新
                        if (!ArticleStatusEnum.PUBLISHING.getCode().equals(newStatus)) {
                            TxwxArticlePO updateArticle = new TxwxArticlePO();
                            updateArticle.setId(article.getId());
                            updateArticle.setStatus(newStatus);
                            updateArticle.setUpdateTime(new Date());
                            articlesToUpdate.add(updateArticle);
                        }
                    }
                } catch (Exception e) {
                    // 单个文章查询失败不影响其他文章的处理
                    System.err.println("查询文章发布状态失败，文章ID: " + article.getId() + ", 错误: " + e.getMessage());
                }
            }

            // 4. 批量更新数据库
            if (!articlesToUpdate.isEmpty()) {
                txwxArticleMapper.batchUpdateStatus(articlesToUpdate);
            }

        } catch (Exception e) {
            throw new RuntimeException("更新发布中文章状态失败: " + e.getMessage(), e);
        }
    }

    /**
     * @description: 将微信发布状态转换为本地文章状态
     */
    private String convertPublishStatusToArticleStatus(Integer publishStatus) {
        if (publishStatus == null) {
            return ArticleStatusEnum.PUBLISHING.getCode();
        }
        switch (publishStatus) {
            case 0:
                return ArticleStatusEnum.PUBLISHED.getCode();
            case 1:
                return ArticleStatusEnum.PUBLISHING.getCode();
            case 2:
                return ArticleStatusEnum.FAIL_ORIGINAL.getCode();
            case 3:
                return ArticleStatusEnum.FAIL_ROUTINE.getCode();
            case 4:
                return ArticleStatusEnum.TENGSEN_REVIEW_APPROVED.getCode();
            case 5:
                return ArticleStatusEnum.DELETE_ALL.getCode();
            case 6:
                return ArticleStatusEnum.BAN_ALL.getCode();
            default:
                return ArticleStatusEnum.PUBLISHING.getCode();
        }
    }
}
