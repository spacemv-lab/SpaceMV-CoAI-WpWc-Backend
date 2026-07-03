package com.txwx.social.crm.publisher;

import com.txwx.social.crm.domain.content.ContentArticle;
import com.txwx.social.crm.domain.content.ContentPublishSiteResponse;

/**
 * 发布适配器接口。
 * 各发布目标（问道路由、微信、小红书、知识星球）各自实现此接口。
 * V1 仅实现 SITE_WENDAO + WECHAT_OFFICIAL_ACCOUNT，
 * 后续新增实现类即可，调度器自动发现。
 */
public interface IContentPublisher {

    /**
     * 获取支持的平台编码。
     */
    String getTargetCode();

    /**
     * 执行发布。
     *
     * @param article 文章
     * @param context 发布上下文（含 targetCode、accountId、stylePreset 等）
     */
    ContentPublishSiteResponse publish(ContentArticle article, PublishContext context);

    /**
     * 获取发布预览内容（各平台格式化后的文本/HTML）。
     *
     * @param article     文章
     * @param stylePreset 样式预设名（可为 null，使用默认）
     */
    default String preview(ContentArticle article, String stylePreset) {
        return article.getContentHtml();
    }
}
