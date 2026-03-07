package com.txwx.webchat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.clickhouse.service.ClickhouseService;
import com.txwx.webchat.domain.entity.DwsUsers;
import com.txwx.webchat.domain.enumType.FilterDimension;
import com.txwx.webchat.domain.vo.UserTotalVo;
import com.txwx.webchat.service.IDataBoardService;
import com.txwx.webchat.service.IDwsUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataBoardServiceImpl implements IDataBoardService {

    @Autowired
    private ClickhouseService clickhouseService;
    @Autowired
    private IDwsUsersService dwsUsersService;

    @Override
    public List<UserTotalVo> selectUserTotal() {
        List<UserTotalVo> res = new ArrayList<>();
        // 获取总用户数
        LambdaQueryWrapper<DwsUsers> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(DwsUsers::getRefDate);
        List<DwsUsers> list = dwsUsersService.list(queryWrapper);
        UserTotalVo userTotalVo = new UserTotalVo();
        userTotalVo.setDesc("总用户数");
        userTotalVo.setValue(list.get(0).getAccumulatedUser());
        res.add(userTotalVo);
        // 获取总阅读数 + 总分享人数 from 2025-11-01
        String readTotalSql = "SELECT sum(read_user_total) as readTotal, sum(share_user) as shareTotal FROM wcai.ods_article_summary_daily";
        List<Map<String, Object>> readData = clickhouseService.readData(readTotalSql);
        if (readData != null && !readData.isEmpty()) {
            Map<String, Object> row = readData.get(0);
            Object readTotal = row.get("readTotal");
            Object shareTotal = row.get("shareTotal");
            userTotalVo = new UserTotalVo();
            userTotalVo.setDesc("总阅读人数");
            userTotalVo.setValue(readTotal != null ? ((Number) readTotal).intValue() : 0);
            res.add(userTotalVo);
            userTotalVo = new UserTotalVo();
            userTotalVo.setDesc("总分享人数");
            userTotalVo.setValue(shareTotal != null ? ((Number) shareTotal).intValue() : 0);
            res.add(userTotalVo);
        }
        return res;
    }

    @Override
    public List<List<Map<String, Object>>> readFlowTrend(Integer filterDimension) {
        StringBuilder readSql = new StringBuilder();
        readSql.append("SELECT ref_date, read_user_total, share_user, ")
                .append("sum(read_user_source_msg) OVER () AS total_source_msg, ")
                .append("sum(read_user_source_chat) OVER () AS total_source_chat, ")
                .append("sum(read_user_source_moments) OVER () AS total_source_moments, ")
                .append("sum(read_user_source_homepage) OVER () AS total_source_homepage, ")
                .append("sum(read_user_source_other) OVER () AS total_source_other, ")
                .append("sum(read_user_source_recommend) OVER () AS total_source_recommend, ")
                .append("sum(read_user_source_search) OVER () AS total_source_search ")
                .append("FROM wcai.ods_article_summary_daily ");
        if (filterDimension != null && filterDimension != FilterDimension.ALL.getCode()){
            readSql.append(" WHERE ref_date >= ");
            switch (filterDimension) {
                case 0: // 7 天
                    readSql.append("subtractDays(today(), 7) ");
                    break;
                case 1: // 30 天
                    readSql.append("subtractDays(today(), 30) ");
                    break;
                case 2: // 半年（180天）
                    readSql.append("subtractDays(today(), 180) ");
                    break;
                case 3:
                    // 1年
                    readSql.append("subtractDays(today(), 365) ");
                    break;
            }
        }
        readSql.append(" ORDER BY ref_date ASC");

        List<Map<String, Object>> list = clickhouseService.readData(readSql.toString());
        List<Map<String, Object>> readTrend = new ArrayList<>();
        List<Map<String, Object>> shareTrend = new ArrayList<>();
        List<Map<String, Object>> sourceSumList = new ArrayList<>();
        for (Map<String, Object> row : list) {
            String date = row.get("ref_date").toString();
            Long reads = ((Number) row.getOrDefault("read_user_total", 0)).longValue();
            Long shares = ((Number) row.getOrDefault("share_user", 0)).longValue();
            HashMap<String, Object> readNumber = new HashMap<>();
            readNumber.put("refDate", date);
            readNumber.put("readerNumber", reads);
            readTrend.add(readNumber);

            HashMap<String, Object> shareNumber = new HashMap<>();
            shareNumber.put("refDate", date);
            shareNumber.put("shareNumber", shares);
            shareTrend.add(shareNumber);
        }
        Map<String, Object> row = list.get(0);
        Object totalSourceMsg = row.get("total_source_msg");
        Object totalSourceChat = row.get("total_source_chat");
        Object totalSourceMoments = row.get("total_source_moments");
        Object totalSourceHomepage = row.get("total_source_homepage");
        Object totalSourceOther = row.get("total_source_other");
        Object totalSourceRecommend = row.get("total_source_recommend");
        Object totalSourceSearch = row.get("total_source_search");

        HashMap<String, Object> userTotalVo = new HashMap();
        userTotalVo.put("公众号消息", totalSourceMsg != null ? ((Number) totalSourceMsg).intValue() : 0);
        sourceSumList.add(userTotalVo);

        userTotalVo = new HashMap<>();
        userTotalVo.put("聊天会话", totalSourceChat != null ? ((Number) totalSourceChat).intValue() : 0);
        sourceSumList.add(userTotalVo);

        userTotalVo = new HashMap<>();
        userTotalVo.put("朋友圈", totalSourceMoments != null ? ((Number) totalSourceMoments).intValue() : 0);
        sourceSumList.add(userTotalVo);

        userTotalVo = new HashMap<>();
        userTotalVo.put("公众号主页", totalSourceHomepage != null ? ((Number) totalSourceHomepage).intValue() : 0);
        sourceSumList.add(userTotalVo);

        userTotalVo = new HashMap<>();
        userTotalVo.put("其他", totalSourceOther != null ? ((Number) totalSourceOther).intValue() : 0);
        sourceSumList.add(userTotalVo);

        userTotalVo = new HashMap<>();
        userTotalVo.put("推荐", totalSourceRecommend != null ? ((Number) totalSourceRecommend).intValue() : 0);
        sourceSumList.add(userTotalVo);

        userTotalVo = new HashMap<>();
        userTotalVo.put("搜一搜", totalSourceSearch != null ? ((Number) totalSourceSearch).intValue() : 0);
        sourceSumList.add(userTotalVo);

        List<List<Map<String, Object>>> res = new ArrayList<>();
        res.add(readTrend);
        res.add(shareTrend);
        res.add(sourceSumList);
        return res;
    }

    @Override
    public List<UserTotalVo> optimumReaderShareNum() {
        String readSql = "SELECT MAX(read_user_total) AS max_read_user_total, MAX(share_user) AS max_share_user from dws_article_read";
        List<Map<String, Object>> list = clickhouseService.readData(readSql);
        UserTotalVo userTotalVo = new UserTotalVo();
        List<UserTotalVo> res = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            Map<String, Object> row = list.get(0);
            Object maxReadUserTotal = row.get("max_read_user_total");
            Object shareUser = row.get("max_share_user");
            userTotalVo.setDesc("最佳阅读人数");
            userTotalVo.setValue(maxReadUserTotal != null ? ((Number)maxReadUserTotal).intValue() : 0);
            res.add(userTotalVo);
            userTotalVo = new UserTotalVo();
            userTotalVo.setDesc("最佳分享人数");
            userTotalVo.setValue(shareUser != null ? ((Number)shareUser).intValue() : 0);
            res.add(userTotalVo);
        }
        return res;
    }

    @Override
    public List<Map<String, Object>> netUserTrend(Integer filterDimension) {
        LocalDate endTime = LocalDate.now();
        LocalDate startTime = null;
        String querySql = null;
        if (filterDimension == FilterDimension.WEEK.getCode()) {
            startTime = endTime.minusWeeks(1);
            querySql = "SELECT net_new_user as netNewUser, ref_date as refDate FROM wcai.dws_users "
                    + "WHERE ref_date >= '"  + startTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    + "' AND ref_date < '" + endTime.format(DateTimeFormatter.ISO_LOCAL_DATE) + "'"
                    + " ORDER BY ref_date ASC";
        }else if (filterDimension == FilterDimension.MONTH.getCode()) {
            startTime = endTime.minusMonths(1);
            querySql = "SELECT net_new_user as netNewUser, ref_date as refDate FROM wcai.dws_users "
                    + "WHERE ref_date >= '"  + startTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    + "' AND ref_date < '" + endTime.format(DateTimeFormatter.ISO_LOCAL_DATE) + "'"
                    + " ORDER BY ref_date ASC";
        }else if (filterDimension == FilterDimension.HALF_YEAR.getCode()) {
            startTime = endTime.minusMonths(6);
            querySql = "SELECT net_new_user as netNewUser, ref_date as refDate FROM wcai.dws_users "
                    + "WHERE ref_date >= '"  + startTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    + "' AND ref_date < '" + endTime.format(DateTimeFormatter.ISO_LOCAL_DATE) + "'"
                    + " ORDER BY ref_date ASC";
        }else if (filterDimension == FilterDimension.YEAR.getCode()){
            startTime = endTime.minusYears(1);
            querySql = "SELECT net_new_user as netNewUser, ref_date as refDate FROM wcai.dws_users "
                    + "WHERE ref_date >= '"  + startTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    + "' AND ref_date < '" + endTime.format(DateTimeFormatter.ISO_LOCAL_DATE) + "'"
                    + " ORDER BY ref_date ASC";
        }else {
            querySql = "SELECT net_new_user as netNewUser, ref_date as refDate FROM wcai.dws_users "
                    + " ORDER BY ref_date ASC";
        }

        List<Map<String, Object>> list = clickhouseService.readData(querySql);
        return list;
    }

    @Override
    public List<Map<String, Object>> accumulatedUserTrend(Integer filterDimension) {
        LocalDate endTime = LocalDate.now();
        LocalDate startTime = null;
        String querySql = null;
        if (filterDimension == FilterDimension.WEEK.getCode()) {
            startTime = endTime.minusWeeks(1);
            querySql = "SELECT accumulated_user as accumulatedUser, ref_date as refDate FROM wcai.dws_users "
                    + "WHERE ref_date >= '"  + startTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    + "' AND ref_date < '" + endTime.format(DateTimeFormatter.ISO_LOCAL_DATE) + "'"
                    + " ORDER BY ref_date ASC";
        }else if (filterDimension == FilterDimension.MONTH.getCode()) {
            startTime = endTime.minusMonths(1);
            querySql = "SELECT accumulated_user as accumulatedUser, ref_date as refDate FROM wcai.dws_users "
                    + "WHERE ref_date >= '"  + startTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    + "' AND ref_date < '" + endTime.format(DateTimeFormatter.ISO_LOCAL_DATE) + "'"
                    + " ORDER BY ref_date ASC";
        }else if (filterDimension == FilterDimension.HALF_YEAR.getCode()) {
            startTime = endTime.minusMonths(6);
            querySql = "SELECT accumulated_user as accumulatedUser, ref_date as refDate FROM wcai.dws_users "
                    + "WHERE ref_date >= '"  + startTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    + "' AND ref_date < '" + endTime.format(DateTimeFormatter.ISO_LOCAL_DATE) + "'"
                    + " ORDER BY ref_date ASC";
        }else if (filterDimension == FilterDimension.YEAR.getCode()){
            startTime = endTime.minusYears(1);
            querySql = "SELECT accumulated_user as accumulatedUser, ref_date as refDate FROM wcai.dws_users "
                    + "WHERE ref_date >= '"  + startTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    + "' AND ref_date < '" + endTime.format(DateTimeFormatter.ISO_LOCAL_DATE) + "'"
                    + " ORDER BY ref_date ASC";
        }else {
            querySql = "SELECT accumulated_user as accumulatedUser, ref_date as refDate FROM wcai.dws_users "
                    + " ORDER BY ref_date ASC";
        }

        List<Map<String, Object>> list = clickhouseService.readData(querySql);
        return list;
    }

    public List<Map<String, Object>> subscribeUserAfterRead() {
        // 获取 已发布的文章
        String readQuerySql = "SELECT \n" +
                "    a.msgid,\n" +
                "    b.title AS article_title,\n" +
                "    a.total_follows,\n" +
                "    a.last_stat_date\n" +
                "FROM (\n" +
                "    -- 1. 统计表：利用 argMax 获取每篇文章在最新日期下的累计关注数\n" +
                "    SELECT \n" +
                "        msgid,\n" +
                "        argMax(read_subscribe_user, stat_date) AS total_follows,\n" +
                "        max(stat_date) AS last_stat_date\n" +
                "    FROM wcai_prod.ods_article_detail_daily\n" +
                "    GROUP BY msgid\n" +
                ") AS a\n" +
                "INNER JOIN (\n" +
                "    -- 2. 维度表：直接通过对齐后的 msgid 关联，同样利用 argMax 取最新标题\n" +
                "    SELECT \n" +
                "        msgid, \n" +
                "        argMax(title, create_time ) AS title \n" +
                "    FROM wcai_prod.ods_article \n" +
                "    GROUP BY msgid\n" +
                ") AS b ON a.msgid = b.msgid\n" +
                "-- 3. 过滤条件：只看有标题的文章，且关注数大于 0\n" +
                "WHERE b.title != '' AND b.title IS NOT NULL AND a.total_follows > 0\n" +
                "-- 4. 排序并取 Top 10\n" +
                "ORDER BY total_follows DESC\n" +
                "LIMIT 10;";
        List<Map<String, Object>> articleList = clickhouseService.readData(readQuerySql);
        return articleList;
    }
}
