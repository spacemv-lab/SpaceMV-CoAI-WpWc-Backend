package com.txwx.social.dashboard.spi.wechat;

import com.ruoyi.common.clickhouse.service.ClickhouseService;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.txwx.social.api.domain.dto.AccountDTO;
import com.txwx.social.api.domain.dto.WriteResultDTO;
import com.txwx.social.api.spi.ISyncServiceProvider;
import com.txwx.social.dashboard.domain.*;
import com.txwx.social.dashboard.domain.entity.OdsArticleDetailDaily;
import com.txwx.social.dashboard.domain.entity.OdsUsers;
import com.txwx.social.dashboard.domain.entity.DwsBizsummaryChannelDaily;
import com.txwx.social.dashboard.remote.AccountRemoteService;
import com.txwx.social.dashboard.util.WebChatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

/**
 * 微信同步服务提供者
 * 实现 ISyncServiceProvider 接口，提供微信数据同步功能
 *
 * @author txwx
 * @date 2026-04-06
 */
@Component
public class WechatSyncProvider implements ISyncServiceProvider {

    private static final Logger logger = LoggerFactory.getLogger(WechatSyncProvider.class);

    @Autowired
    private ClickhouseService clickhouseService;

    @Autowired
    private AccountRemoteService accountRemoteService;

    @Override
    public String getChannelType() {
        return "wechat";
    }

    @Override
    public String getAccessToken(Long accountId) {
        // 从 CRM 服务获取账号信息
        AjaxResult accountResult = accountRemoteService.getAccountById(accountId);
        if (accountResult == null || accountResult.get(AjaxResult.DATA_TAG) == null) {
            logger.error("获取账号信息失败，accountId: {}", accountId);
            return null;
        }

        AccountDTO account = (AccountDTO) accountResult.get(AjaxResult.DATA_TAG);
        String appid = account.getAppid();
        String secret = account.getSecret();

        if (appid == null || appid.isEmpty() || secret == null || secret.isEmpty()) {
            logger.error("账号配置缺失，accountId: {}", accountId);
            return null;
        }

        return WebChatUtil.getAccessToken(appid, secret);
    }

    @Override
    public Map<String, List<?>> fetchData(LocalDate startDate, LocalDate endDate, Long accountId) {
        String accessToken = getAccessToken(accountId);
        if (accessToken == null || accessToken.isEmpty()) {
            logger.error("获取微信 access_token 失败");
            return Collections.emptyMap();
        }

        String startDateStr = startDate.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
        String endDateStr = endDate.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);

        Map<String, List<?>> result = new HashMap<>();

        try {
            // 1. 用户数据
            List<WebChatUser> users = WebChatUtil.getUserYesterday(accessToken, startDateStr, endDateStr);
            result.put("users", users);

            // 2. 文章概况数据
            List<ArticleSummaryDaily> articleSummary = WebChatUtil.getArticleSummaryDaily(accessToken, startDateStr, endDateStr);
            result.put("article_summary", articleSummary);

            // 3. 文章阅读数据
            List<ArticleReadDaily> articleRead = WebChatUtil.getArticleReadDaily(accessToken, startDateStr, endDateStr);
            result.put("article_read", articleRead);

            // 4. 文章分享数据
            List<ArticleShareDaily> articleShare = WebChatUtil.getArticleShareDaily(accessToken, startDateStr, endDateStr);
            result.put("article_share", articleShare);

            // 5. 文章详情数据
            List<ArticleDetailDaily> articleDetail = WebChatUtil.getArticleDetailDaily(accessToken, startDateStr, endDateStr);
            result.put("article_detail", articleDetail);

            logger.info("获取微信数据成功: {}", result.keySet());

        } catch (Exception e) {
            logger.error("获取微信数据失败", e);
            // 即使部分数据获取失败，也返回已获取的数据
        }

        return result;
    }

    @Override
    public WriteResultDTO writeData(Map<String, List<?>> dataMap, Long accountId) {
        if (dataMap == null || dataMap.isEmpty()) {
            return WriteResultDTO.builder()
                    .success(true)
                    .count(0)
                    .errorMessage("数据为空")
                    .build();
        }

        int totalCount = 0;
        String errorMsg = "";

        try {
            // 1. 写入用户数据到 ods_users
            List<?> users = dataMap.get("users");
            if (users != null && !users.isEmpty()) {
                writeOdsUsers(users, accountId);
                totalCount += users.size();
            }

            // 2. 写入文章概况数据到 ods_article_summary_daily
            List<?> articleSummary = dataMap.get("article_summary");
            if (articleSummary != null && !articleSummary.isEmpty()) {
                writeOdsArticleSummary(articleSummary, accountId);
                totalCount += articleSummary.size();
            }

            // 3. 写入文章阅读数据到 ods_article_read_daily
            List<?> articleRead = dataMap.get("article_read");
            if (articleRead != null && !articleRead.isEmpty()) {
                writeOdsArticleRead(articleRead, accountId);
                totalCount += articleRead.size();
            }

            // 4. 写入文章分享数据到 ods_article_share_daily
            List<?> articleShare = dataMap.get("article_share");
            if (articleShare != null && !articleShare.isEmpty()) {
                writeOdsArticleShare(articleShare, accountId);
                totalCount += articleShare.size();
            }

            // 5. 写入文章详情数据到 ods_article_detail_daily
            List<?> articleDetail = dataMap.get("article_detail");
            if (articleDetail != null && !articleDetail.isEmpty()) {
                writeOdsArticleDetail(articleDetail, accountId);
                totalCount += articleDetail.size();
            }

            logger.info("写入 ClickHouse 数据成功, 总条数: {}", totalCount);

        } catch (Exception e) {
            logger.error("写入 ClickHouse 数据失败", e);
            errorMsg = e.getMessage();
        }

        return WriteResultDTO.builder()
                .success(errorMsg.isEmpty())
                .count(totalCount)
                .errorMessage(errorMsg)
                .build();
    }

    /**
     * 写入用户数据到 ods_users
     */
    private void writeOdsUsers(List<?> data, Long accountId) {
        String sql = "INSERT INTO ods_users (ref_date, user_source, new_user, cancel_user, account_id) VALUES (?, ?, ?, ?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();
        for (Object item : data) {
            if (item instanceof WebChatUser) {
                WebChatUser user = (WebChatUser) item;
                batchArgs.add(new Object[]{
                    user.getRef_date(),
                    user.getUser_source(),
                    user.getNew_user(),
                    user.getCancel_user(),
                    accountId
                });
            }
        }
        if (!batchArgs.isEmpty()) {
            clickhouseService.batchInsert(sql, batchArgs);
        }
    }

    /**
     * 写入文章概况数据到 ods_article_summary_daily
     */
    private void writeOdsArticleSummary(List<?> data, Long accountId) {
        String sql = "INSERT INTO ods_article_summary_daily (stat_date, ref_date, msgid, title, publish_type, read_user, share_user, zaikan_user, like_user, comment_count, collection_user, praise_money, read_subscribe_user, read_delivery_rate, read_finish_rate, read_avg_activetime, url, platform_id, account_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();
        for (Object item : data) {
            if (item instanceof ArticleSummaryDaily) {
                ArticleSummaryDaily article = (ArticleSummaryDaily) item;
                // 转换为 ods 表格式
                batchArgs.add(article.toObject(accountId, accountId));
            }
        }
        if (!batchArgs.isEmpty()) {
            clickhouseService.batchInsert(sql, batchArgs);
        }
    }

    /**
     * 写入文章阅读数据到 ods_article_read_daily
     */
    private void writeOdsArticleRead(List<?> data, Long accountId) {
        String sql = "INSERT INTO ods_article_read_daily (stat_date, ref_date, msgid, title, read_user, read_user_source_all, read_user_source_msg, read_user_source_chat, read_user_source_moments, read_user_source_homepage, read_user_source_other, read_user_source_recommend, read_user_source_search, url, platform_id, account_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();
        for (Object item : data) {
            if (item instanceof ArticleReadDaily) {
                ArticleReadDaily article = (ArticleReadDaily) item;
                //batchArgs.add(article.toObject(accountId, accountId));
            }
        }
        if (!batchArgs.isEmpty()) {
            clickhouseService.batchInsert(sql, batchArgs);
        }
    }

    /**
     * 写入文章分享数据到 ods_article_share_daily
     */
    private void writeOdsArticleShare(List<?> data, Long accountId) {
        String sql = "INSERT INTO ods_article_share_daily (stat_date, ref_date, msgid, title, share_user, share_user_source_all, share_user_source_chat, share_user_source_moments, share_user_source_other, share_user_source_recommend, share_user_source_search, url, platform_id, account_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();
        for (Object item : data) {
            if (item instanceof ArticleShareDaily) {
                ArticleShareDaily article = (ArticleShareDaily) item;
                //batchArgs.add(article.toObject(accountId, accountId));
            }
        }
        if (!batchArgs.isEmpty()) {
            clickhouseService.batchInsert(sql, batchArgs);
        }
    }

    /**
     * 写入文章详情数据到 ods_article_detail_daily
     */
    private void writeOdsArticleDetail(List<?> data, Long accountId) {
        String sql = "INSERT INTO ods_article_detail_daily (stat_date, ref_date, msgid, publish_type, read_user, read_user_source_all, read_user_source_msg, read_user_source_chat, read_user_source_moments, read_user_source_homepage, read_user_source_other, read_user_source_recommend, read_user_source_search, share_user, zaikan_user, like_user, comment_count, collection_user, praise_money, read_subscribe_user, read_delivery_rate, read_finish_rate, read_avg_activetime, title, url, platform_id, account_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();
        for (Object item : data) {
            if (item instanceof ArticleDetailDaily) {
                ArticleDetailDaily article = (ArticleDetailDaily) item;
                // 扁平化处理
                if (article.toFlattenObjectList(accountId, accountId) != null) {
                    batchArgs.addAll(article.toFlattenObjectList(accountId, accountId));
                }
            }
        }
        if (!batchArgs.isEmpty()) {
            clickhouseService.batchInsert(sql, batchArgs);
        }
    }
}
