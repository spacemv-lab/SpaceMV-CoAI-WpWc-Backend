package com.txwx.social.dashboard.service;


import com.ruoyi.common.clickhouse.service.ClickhouseService;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.StringUtils;
import com.txwx.social.dashboard.config.WebChatConfig;
import com.txwx.social.dashboard.domain.ArticleDetailDaily;
import com.txwx.social.dashboard.domain.ArticleSummaryDaily;
import com.txwx.social.dashboard.domain.WebChatUser;
import com.txwx.social.dashboard.domain.entity.DwsBizsummaryChannelDaily;
import com.txwx.social.dashboard.domain.mapper.DwsBizsummaryChannelDailyMapper;
import com.txwx.social.dashboard.domain.mapper.DwsContentDataMapper;
import com.txwx.social.dashboard.util.WebChatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SyncDataService {

    @Autowired
    private WebChatConfig webChatConfig;
    @Autowired
    private ClickhouseService clickhouseService;
    @Autowired
    private DwsBizsummaryChannelDailyMapper dwsBizsummaryChannelDailyMapper;
    @Autowired
    private DwsContentDataMapper dwsContentDataMapper;

    public void syncUserHistoryRange(String accessToken, Long accountId, String startDate, String endDate) {
        if (StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
            throw new ServiceException("开始日期和结束日期不能为空");
        }

        LocalDate start;
        LocalDate end;
        try {
            start = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
            end = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            throw new ServiceException("日期格式错误，请使用 yyyy-MM-dd");
        }

        if (start.isAfter(end)) {
            throw new ServiceException("开始日期不能大于结束日期");
        }

        LocalDate today = LocalDate.now();
        if (end.isAfter(today)) {
            end = today;
        }

        log.info("开始执行微信公众号历史区间同步, startDate={}, endDate={}", start, end);

        LocalDate current = start;
        while (!current.isAfter(end)) {
            try {
                syncUserOneDay(accessToken, accountId, current);
            } catch (Exception e) {
                log.error("历史区间同步失败, refDate={}, msg={}", current, e.getMessage(), e);
            }
            current = current.plusDays(1);
        }

        log.info("微信公众号历史区间同步完成, startDate={}, endDate={}", start, end);
    }

    public void syncArticleSummaryHistoryRange(String accessToken, Long accountId, String startDate, String endDate) {
        if (StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
            throw new ServiceException("开始日期和结束日期不能为空");
        }

        LocalDate start;
        LocalDate end;
        try {
            start = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
            end = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            throw new ServiceException("日期格式错误，请使用 yyyy-MM-dd");
        }

        if (start.isAfter(end)) {
            throw new ServiceException("开始日期不能大于结束日期");
        }

        LocalDate today = LocalDate.now();
        if (end.isAfter(today)) {
            end = today;
        }

        log.info("开始执行发表内容概况总数据历史区间同步, startDate={}, endDate={}", start, end);

        LocalDate current = start;
        while (!current.isAfter(end)) {
            try {
                syncArticleSummaryDailyOneDay(accessToken, accountId, current);
            } catch (Exception e) {
                log.error("历史区间同步失败, refDate={}, msg={}", current, e.getMessage(), e);
            }
            current = current.plusDays(1);
        }

        log.info("发表内容概况总数据历史区间同步完成, startDate={}, endDate={}", start, end);
    }

    public void syncArticleTotalDetailHistoryRange(String accessToken, Long accountId, String startDate, String endDate) {
        log.info("<##############################发表内容发表详细数据历史区间同步开始##############################>");
        log.info("传入的凭证->{}", accessToken);
        log.info("开始日期={}, 结束日期={}", startDate, endDate);

        if (StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
            throw new ServiceException("开始日期和结束日期不能为空");
        }

        LocalDate start;
        LocalDate end;
        try {
            start = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
            end = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            throw new ServiceException("日期格式错误，请使用 yyyy-MM-dd");
        }

        if (start.isAfter(end)) {
            throw new ServiceException("开始日期不能大于结束日期");
        }

        LocalDate today = LocalDate.now();
        if (end.isAfter(today)) {
            end = today;
        }

        LocalDate current = start;
        while (!current.isAfter(end)) {
            try {
                syncArticleDetailOneDay(accessToken, accountId, current);
            } catch (Exception e) {
                log.error("历史区间同步失败, baseDate={}, msg={}", current, e.getMessage(), e);
            }
            current = current.plusDays(1);
        }

        // 区间全部同步完成后，统一聚合一次
        try {
            dwsContentDataMapper.truncateDwsContentData();
            dwsContentDataMapper.aggregateArticleDetailsDataToDws();
            log.info("成功聚合文章详细数据到 dws_content_data");
        } catch (Exception ex) {
            log.error("聚合 dws_content_data 失败: {}", ex.getMessage(), ex);
        }

        log.info("<##############################发表内容发表详细数据历史区间同步结束##############################>");
    }

    public void syncUserOneDay(String accessToken, Long accountId, LocalDate refDate) {
        String refDateStr = refDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        log.info("<##############################微信公众号用户抓取开始，日期：{} ##############################>", refDateStr);
        log.info("传入的凭证->{}", accessToken);

        // 1. 抓取指定日期数据
        List<WebChatUser> userDayTemp = null;
        try {
            userDayTemp = WebChatUtil.getUserWithDate(accessToken, refDateStr, refDateStr);
        } catch (Exception ex) {
            log.error("抓取微信公众号关注或取消用户失败, refDate={}, msg={}", refDateStr, ex.getMessage(), ex);
        }
        final List<WebChatUser> userDay = userDayTemp;

        // 2. 写入 ods_users
        if (userDay != null && !userDay.isEmpty()) {
            log.info("<------获取的用户数据条数------> {}", userDay.size());
            log.info("<------获取的用户数据------> {}", userDay);

            String deleteOdsSql = "DELETE FROM ods_users WHERE accountId = ? AND ref_date = ?";
            clickhouseService.singleInsert(deleteOdsSql, accountId, refDateStr);

            List<Object[]> batchArgs = new ArrayList<>();
            for (WebChatUser user : userDay) {
                batchArgs.add(user.toObject(accountId));
            }

            clickhouseService.batchInsert(webChatConfig.getInsertusersql(), batchArgs);
        } else {
            log.warn("refDate={} 未获取到公众号用户数据", refDateStr);
            // 历史补数建议也清理当天旧数据，避免脏数据残留
            String deleteOdsSql = "DELETE FROM ods_users WHERE account_id = ? AND ref_date = ?";

            clickhouseService.singleInsert(deleteOdsSql, accountId, refDateStr);
        }

        // 3. 汇总写入 dws_users
        try {
            int totalNewUser = 0;
            int totalCancelUser = 0;

            if (userDay != null && !userDay.isEmpty()) {
                totalNewUser = userDay.stream().mapToInt(WebChatUser::getNew_user).sum();
                totalCancelUser = userDay.stream().mapToInt(WebChatUser::getCancel_user).sum();
            }

            log.info("refDate={}, 累加new_user={}, 累加cancel_user={}", refDateStr, totalNewUser, totalCancelUser);

            int netNewUser = totalNewUser - totalCancelUser;
            log.info("refDate={}, 净增新用户数net_new_user={}", refDateStr, netNewUser);

            int dbUserSource = 0;
            int dbCancelSource = 0;
            try {
                String querySql = "SELECT sum(new_user) as total_new, sum(cancel_user) as total_cancel " +
                        "FROM ods_users WHERE account_id = ? and ref_date < ?";
                List<Map<String, Object>> result = clickhouseService.readData(querySql, accountId, refDateStr);

                if (result != null && !result.isEmpty()) {
                    Map<String, Object> row = result.get(0);
                    Object totalNew = row.get("total_new");
                    Object totalCancel = row.get("total_cancel");
                    dbUserSource = totalNew != null ? ((Number) totalNew).intValue() : 0;
                    dbCancelSource = totalCancel != null ? ((Number) totalCancel).intValue() : 0;
                    log.info("refDate={}, 历史累计new_user={}, 历史累计cancel_user={}", refDateStr, dbUserSource, dbCancelSource);
                }
            } catch (Exception ex) {
                log.error("查询ods_users历史累计数据失败, refDate={}, msg={}", refDateStr, ex.getMessage(), ex);
            }

            int accumulatedUser = dbUserSource - dbCancelSource + netNewUser;
            log.info("refDate={}, 历史累计净增用户accumulated_user={}", refDateStr, accumulatedUser);

            List<Object[]> dwsBatchArgs = new ArrayList<>();
            dwsBatchArgs.add(new Object[]{
                    refDateStr,
                    totalNewUser,
                    totalCancelUser,
                    netNewUser,
                    accumulatedUser,
                    accountId
            });

            String insertDwsSql = webChatConfig.getInsertdwsuserssql();
            if (insertDwsSql != null && !insertDwsSql.isEmpty()) {
                try {
                    String deleteDwsSql = "DELETE FROM dws_users WHERE account_id = ? AND ref_date = ?";
                    clickhouseService.singleInsert(deleteDwsSql, accountId, refDate);
                    clickhouseService.batchInsert(insertDwsSql, dwsBatchArgs);
                    log.info("成功插入dws_users表数据, refDate={}", refDateStr);
                } catch (Exception ex) {
                    log.error("插入dws_users表失败, refDate={}, msg={}", refDateStr, ex.getMessage(), ex);
                }
            } else {
                log.warn("未配置insertdwsuserssql，无法插入数据到dws_users表, refDate={}", refDateStr);
            }
        } catch (Exception ex) {
            log.error("处理dws_users数据汇总失败, refDate={}, msg={}", refDateStr, ex.getMessage(), ex);
        }

        log.info("<##############################微信公众号用户抓取结束，日期：{} ##############################>", refDateStr);
    }

    public void syncArticleSummaryDailyOneDay(String accessToken, Long accountId, LocalDate refDate) {
        String refDateStr = refDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        log.info("<##############################发表内容概况总数据抓取开始，日期：{} ##############################>", refDateStr);
        log.info("传入的凭证->{}", accessToken);

        // 1. 抓取指定日期数据
        List<ArticleSummaryDaily> articleSummaryDailyList = null;
        try {
            articleSummaryDailyList = WebChatUtil.getArticleSummaryDaily(accessToken, refDateStr, refDateStr);
        } catch (Exception ex) {
            log.error("抓取发表内容概况总数据失败, refDate={}, msg={}", refDateStr, ex.getMessage(), ex);
        }

        // 2. 写入 ClickHouse 明细表
        if (articleSummaryDailyList != null && !articleSummaryDailyList.isEmpty()) {
            log.info("<------获取的发表内容概况总数据条数------> {}", articleSummaryDailyList.size());
            log.info("<------获取的发表内容概况总数据------> {}", articleSummaryDailyList);

            List<Object[]> batchArgs = new ArrayList<>();
            for (ArticleSummaryDaily article : articleSummaryDailyList) {
                batchArgs.add(article.toObject(accountId));
            }

            String insertSql = webChatConfig.getInsertarticlesummarydailysql();
            if (insertSql != null && !insertSql.isEmpty()) {
                try {
                    String deleteSql = "DELETE FROM ods_article_summary_daily WHERE account_id =? AND ref_date = ?";
                    clickhouseService.singleInsert(deleteSql, accountId, refDateStr);

                    clickhouseService.batchInsert(insertSql, batchArgs);
                    log.info("成功插入发表内容概况总数据到ClickHouse，refDate={}, 数量={}", refDateStr, batchArgs.size());
                } catch (Exception ex) {
                    log.error("插入发表内容概况总数据到ClickHouse失败, refDate={}, msg={}", refDateStr, ex.getMessage(), ex);
                }
            } else {
                log.warn("未配置insertarticlesummarydailysql，无法插入数据到ClickHouse, refDate={}", refDateStr);
            }
        } else {
            log.warn("refDate={} 未获取到发表内容概况总数据", refDateStr);
            try {
                // 没取到数据时，也清理当天旧数据，避免脏数据残留
                String deleteSql = "DELETE FROM ods_article_summary_daily WHERE account_id =? AND ref_date = ?";
                clickhouseService.singleInsert(deleteSql, accountId, refDateStr);
            } catch (Exception ex) {
                log.error("清理发表内容概况总数据失败, refDate={}, msg={}", refDateStr, ex.getMessage(), ex);
            }
        }

        // 3. 汇聚到 dws_bizsummary_channel_daily
        try {
            String deleteSql = "DELETE FROM dws_bizsummary_channel_daily WHERE account_id =? AND ref_date = ?";
            clickhouseService.singleInsert(deleteSql, accountId, refDateStr);

            if (articleSummaryDailyList != null && !articleSummaryDailyList.isEmpty()) {
                List<DwsBizsummaryChannelDaily> dwsBatchArgs = new ArrayList<>();
                for (ArticleSummaryDaily article : articleSummaryDailyList) {
                    dwsBatchArgs.addAll(article.toDwsContentData(accountId));
                }

                if (!dwsBatchArgs.isEmpty()) {
                    dwsBizsummaryChannelDailyMapper.insertBatch(dwsBatchArgs);
                    log.info("成功汇聚到 dws_bizsummary_channel_daily，refDate={}, 数量={}", refDateStr, dwsBatchArgs.size());
                }
            }
        } catch (Exception ex) {
            log.error("汇聚 dws_bizsummary_channel_daily 失败, refDate={}, msg={}", refDateStr, ex.getMessage(), ex);
        }

        log.info("<##############################发表内容概况总数据抓取结束，日期：{} ##############################>", refDateStr);
    }

    public void syncArticleDetailOneDay(String accessToken, Long accountId, LocalDate baseDate) {
        log.info("<##############################发表内容发表详细数据单天同步开始##############################>");
        log.info("传入的凭证->{}", accessToken);
        log.info("基准日期->{}", baseDate);

        String insertSql = webChatConfig.getInsertarticledetaildailysql();
        if (insertSql == null || insertSql.isEmpty()) {
            log.error("ClickHouse 插入 SQL 未配置，任务终止");
            return;
        }

        List<Object[]> allBatchArgs = new ArrayList<>();
        LocalDate startDate = baseDate.minusDays(30);
        LocalDate endDate = baseDate.minusDays(1);

        log.info("本次回溯区间: {} ~ {}", startDate, endDate);

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            String refDateStr = current.format(DateTimeFormatter.ISO_LOCAL_DATE);
            log.info("正在抓取发布日期为 [{}] 的文章数据", refDateStr);

            List<ArticleDetailDaily> articleDetailDailyList = null;
            try {
                articleDetailDailyList = WebChatUtil.getArticleDetailDaily(accessToken, refDateStr, refDateStr);
            } catch (Exception ex) {
                log.error("抓取发表内容发表详细数据失败, refDate={}, msg={}", refDateStr, ex.getMessage(), ex);
            }

            if (articleDetailDailyList != null && !articleDetailDailyList.isEmpty()) {
                log.info("refDate={}, 获取的发表内容发表详细数据条数={}", refDateStr, articleDetailDailyList.size());
                for (ArticleDetailDaily articleDetailDaily : articleDetailDailyList) {
                    allBatchArgs.addAll(articleDetailDaily.toFlattenObjectList(accountId));
                }
            }

            current = current.plusDays(1);
        }

        try {
            // 删除本次回溯区间内的旧数据，再写入最新数据
            String deleteSql = "DELETE FROM ods_article_detail_daily WHERE account_id = ? AND ref_date >= ? AND ref_date <= ?";
            clickhouseService.singleInsert(deleteSql, accountId, startDate, endDate);

            if (!allBatchArgs.isEmpty()) {
                clickhouseService.batchInsert(insertSql, allBatchArgs);
                log.info("成功插入发表内容发表详细数据到ClickHouse，数量={}", allBatchArgs.size());
            } else {
                log.warn("本次回溯区间内未获取到任何文章详细数据");
            }
        } catch (Exception ex) {
            log.error("插入发表内容发表详细数据到ClickHouse失败: {}", ex.getMessage(), ex);
        }

        log.info("<##############################发表内容发表详细数据单天同步结束##############################>");
    }
}

