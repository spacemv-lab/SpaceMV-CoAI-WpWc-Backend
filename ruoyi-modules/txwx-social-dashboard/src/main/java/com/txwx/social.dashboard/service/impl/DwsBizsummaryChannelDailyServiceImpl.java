package com.txwx.social.dashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.clickhouse.service.ClickhousePageHelper;
import com.ruoyi.common.clickhouse.service.ClickhouseService;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.txwx.social.dashboard.domain.condition.FlowSearchCondition;
import com.txwx.social.dashboard.domain.entity.DwsBizsummaryChannelDaily;
import com.txwx.social.dashboard.mapper.DwsBizsummaryChannelDailyMapper;
import com.txwx.social.dashboard.domain.entity.FlowSource;
import com.txwx.social.dashboard.service.IDwsBizsummaryChannelDailyService;
import com.txwx.social.dashboard.util.DataConvertUtil;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class DwsBizsummaryChannelDailyServiceImpl extends ServiceImpl<DwsBizsummaryChannelDailyMapper, DwsBizsummaryChannelDaily> implements IDwsBizsummaryChannelDailyService {

    @Autowired
    private ClickhousePageHelper clickhousePageHelper;

    @Autowired
    private ClickhouseService clickhouseService;

    public TableDataInfo selectByCondition(FlowSearchCondition condition) {
        String baseSql = "SELECT " +
                "    ref_date, " +
                "    SUM(read_user_total) as read_user_total, " +
                "    SUM(share_user) as share_user, " +
                "    SUM(collection_user) as collection_user, " +
                "    SUM(read_user_source_chat) as read_user_source_chat, " +
                "    SUM(read_user_source_homepage) as read_user_source_homepage, " +
                "    SUM(read_user_source_moments) as read_user_source_moments, " +
                "    SUM(read_user_source_msg) as read_user_source_msg, " +
                "    SUM(read_user_source_other) as read_user_source_other, " +
                "    SUM(read_user_source_recommend) as read_user_source_recommend, " +
                "    SUM(read_user_source_search) as read_user_source_search " +
                "FROM ods_article_summary_daily  " +
                "WHERE account_id = ? ";
        return queryByCondition(baseSql, condition);
    }

    private TableDataInfo queryByCondition(String baseSql, FlowSearchCondition condition) {
        StringBuilder stringBuilder = new StringBuilder(baseSql);
        TableDataInfo resList = null;
        if (condition != null) {
            if (condition.getStartTime() != null && condition.getEndTime() != null) {
                stringBuilder.append("AND ref_date BETWEEN ? AND ? ");
                stringBuilder.append(" GROUP BY ref_date ORDER BY ref_date DESC");
                resList = clickhousePageHelper.queryForPage(stringBuilder.toString(), condition.getPageNum(), condition.getPageSize(),
                        condition.getAccountId(), condition.getStartTime(), condition.getEndTime());
            } else {
                stringBuilder.append(" GROUP BY ref_date ORDER BY ref_date DESC");
                resList = clickhousePageHelper.queryForPage(stringBuilder.toString(), condition.getPageNum(), condition.getPageSize(),
                        condition.getAccountId());
            }
        }
        return resList;
    }

    @Override
    public List<FlowSource> selectSource(Long accountId) {
        // 1. 从ClickHouse获取数据
        String sourceSql = "SELECT sum(read_user_source_recommend) as recommend, sum(read_user_source_msg) as msg, " +
                "sum(read_user_source_homepage) as homepage, sum(read_user_source_chat) as chat, " +
                "sum(read_user_source_moments) as moments, sum(read_user_source_other) as other, " +
                "sum(read_user_source_search) as search_cnt FROM ods_article_summary_daily where account_id=?";
        List<Map<String, Object>> dataList = clickhouseService.readData(sourceSql, accountId);
        Map<String, Object> row = dataList.get(0);
        List<FlowSource> resList = Lists.newArrayList();
        double sum = 0;
        for (String key : row.keySet()){
            String source = getSourceName(key);
            if (StringUtils.isEmpty(source)) {
                continue;
            }
            Object readNum = row.get(key);
            long count = readNum != null ? ((Number) readNum).longValue() : 0;
            sum += count;
            FlowSource flowSource = new FlowSource();
            flowSource.setChannel(source);
            flowSource.setReadUser(count);
            resList.add(flowSource);
        }

        for (FlowSource flowSource : resList) {
            flowSource.setProportion(String.format("%.2f%%", (flowSource.getReadUser() * 100.0) / sum));
        }
        // 2. 使用工具类转换
        return resList;
    }

    private String getSourceName(String key) {
        String source;
        switch (key) {
            case "recommend" -> source = "推荐";
            case "msg" -> source = "公众号消息";
            case "homepage" -> source = "公众号主页";
            case "chat" -> source = "聊天会话";
            case "moments" -> source = "朋友圈";
            case "other" -> source = "其他";
            case "search_cnt" -> source = "搜一搜";
            default -> source = StringUtils.EMPTY;
        }
        return source;
    }


}
