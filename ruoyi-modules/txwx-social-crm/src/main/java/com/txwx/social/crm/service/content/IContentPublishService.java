package com.txwx.social.crm.service.content;

import com.txwx.social.crm.controller.content.PublishTargetDTO;
import com.txwx.social.crm.domain.content.ContentPublishJob;
import com.txwx.social.crm.domain.content.ContentPublishResult;
import com.txwx.social.crm.domain.content.ContentPublishSiteResponse;

import java.util.List;

public interface IContentPublishService {

    ContentPublishSiteResponse publishToSite(Long articleId);

    List<ContentPublishSiteResponse> publishToMulti(Long articleId, List<PublishTargetDTO> targets);

    /**
     * 保存文章到微信公众号草稿箱（不发布）。
     */
    void saveDraftToWeChat(Long articleId, Long accountId, String stylePreset);

    List<ContentPublishJob> selectJobsByArticleId(Long articleId);

    List<ContentPublishResult> selectResultsByArticleId(Long articleId);

    List<ContentPublishJob> selectJobList(ContentPublishJob query);

    ContentPublishJob selectJobById(Long id);

    List<ContentPublishResult> selectResultList(ContentPublishResult query);

    ContentPublishResult selectResultById(Long id);

}
