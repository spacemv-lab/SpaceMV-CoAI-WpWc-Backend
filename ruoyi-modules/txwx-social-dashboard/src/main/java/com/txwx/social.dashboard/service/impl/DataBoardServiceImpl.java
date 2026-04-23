package com.txwx.social.dashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.clickhouse.service.ClickhouseService;
import com.txwx.social.dashboard.domain.entity.DwsContentData;
import com.txwx.social.dashboard.domain.entity.DwsUsers;
import com.txwx.social.dashboard.domain.enums.FilterDimension;
import com.txwx.social.dashboard.mapper.DwsBizsummaryChannelDailyMapper;
import com.txwx.social.dashboard.mapper.DwsContentDataMapper;
import com.txwx.social.dashboard.domain.vo.UserTotalVo;
import com.txwx.social.dashboard.service.IDataBoardService;
import com.txwx.social.dashboard.service.IDwsUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DataBoardServiceImpl implements IDataBoardService {

    @Autowired
    private ClickhouseService clickhouseService;
    @Autowired
    private IDwsUsersService dwsUsersService;
    @Autowired
    private DwsContentDataMapper dwsContentDataMapper;
    @Autowired
    private DwsBizsummaryChannelDailyMapper dwsBizsummaryChannelDailyMapper;

    @Override
    public List<UserTotalVo> selectUserTotal(Long accountId) {
        List<UserTotalVo> res = new ArrayList<>();
        // 获取总用户数
        LambdaQueryWrapper<DwsUsers> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(DwsUsers::getRefDate);
        // 增加平台控制
        queryWrapper.eq(DwsUsers::getAccountId, accountId);
        List<DwsUsers> list = dwsUsersService.list(queryWrapper);
        UserTotalVo userTotalVo = new UserTotalVo();
        userTotalVo.setDesc("总用户数");
        userTotalVo.setValue(0L);
        if (!CollectionUtils.isEmpty(list)) {
            userTotalVo.setValue(list.get(0).getAccumulatedUser());
        }
        res.add(userTotalVo);
        Map<String, Long> map = dwsBizsummaryChannelDailyMapper.selectTotalReadShare(accountId);
        if (map != null && !map.isEmpty()) {
            Object readTotal = map.get("readTotal");
            Object shareTotal = map.get("shareTotal");
            userTotalVo = new UserTotalVo();
            userTotalVo.setDesc("总阅读人数");
            userTotalVo.setValue(readTotal != null ? ((Number) readTotal).longValue() : 0);
            res.add(userTotalVo);
            userTotalVo = new UserTotalVo();
            userTotalVo.setDesc("总分享人数");
            userTotalVo.setValue(shareTotal != null ? ((Number) shareTotal).longValue() : 0);
            res.add(userTotalVo);
        }
        return res;
    }

    @Override
    public List<List<Map<String, Object>>> readFlowTrend(Integer filterDimension, Long accountId) {
        String readSql;
        Integer minussDays = FilterDimension.calcMinusDays(filterDimension);
        List<Map<String, Object>> list;
        if (minussDays != null) {
            readSql = "SELECT ref_date, read_user_cnt, share_user " +
                    "FROM dws_bizsummary_channel_daily " +
                    "WHERE channel = '全部'" +
                    " and account_id = ?" +
                    " and ref_date >= " +
                    "subtractDays(today(), ?) " +
                    " ORDER BY ref_date ASC";
            list = clickhouseService.readData(readSql, accountId, minussDays);
        } else {
            readSql = "SELECT ref_date, read_user_cnt, share_user " +
                    "FROM dws_bizsummary_channel_daily " +
                    "WHERE channel = '全部'" +
                    " and account_id = ?" +
                    " ORDER BY ref_date ASC";
            list = clickhouseService.readData(readSql, accountId);
        }


        List<Map<String, Object>> readTrend = new ArrayList<>();
        List<Map<String, Object>> shareTrend = new ArrayList<>();
        List<Map<String, Object>> sourceSumList = new ArrayList<>();
        for (Map<String, Object> row : list) {
            String date = row.get("ref_date").toString();
            Long reads = ((Number) row.getOrDefault("read_user_cnt", 0)).longValue();
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

        List<Map<String, Object>> temp = dwsBizsummaryChannelDailyMapper.selectDataBoardReadSource(accountId);
        HashMap<String, Object> userTotalVo = null;
        for (Map<String, Object> row : temp){
            userTotalVo = new HashMap<>();
            String source = row.get("channel_fixed").toString();
            Object readNum = row.get("read_users");
            userTotalVo.put(source, readNum != null ? ((Number) readNum).longValue() : 0);
            sourceSumList.add(userTotalVo);
        }
        List<List<Map<String, Object>>> res = new ArrayList<>();
        res.add(readTrend);
        res.add(shareTrend);
        res.add(sourceSumList);
        return res;
    }

    @Override
    public List<UserTotalVo> optimumReaderShareNum(Long accountId) {
        String readSql = "SELECT MAX(read_user_total) AS max_read_user_total, MAX(share_user) AS max_share_user from dws_content_data where account_id = ?";
        List<Map<String, Object>> list = clickhouseService.readData(readSql, accountId);
        UserTotalVo userTotalVo = new UserTotalVo();
        List<UserTotalVo> res = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            Map<String, Object> row = list.get(0);
            Object maxReadUserTotal = row.get("max_read_user_total");
            Object shareUser = row.get("max_share_user");
            userTotalVo.setDesc("最佳阅读人数");
            userTotalVo.setValue(maxReadUserTotal != null ? ((Number)maxReadUserTotal).longValue() : 0);
            res.add(userTotalVo);
            userTotalVo = new UserTotalVo();
            userTotalVo.setDesc("最佳分享人数");
            userTotalVo.setValue(shareUser != null ? ((Number)shareUser).longValue() : 0);
            res.add(userTotalVo);
        }
        return res;
    }

    @Override
    public List<Map<String, Object>> netUserTrend(Integer filterDimension, Long accountId) {
        LocalDate endTime = LocalDate.now();
        String endTimeStr = endTime.format(DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate startTime = FilterDimension.calculateStartTimeEnhanced(filterDimension);
        String querySql = null;
        if (startTime != null) {
            String startTimeStr = startTime
                .format(DateTimeFormatter.ISO_LOCAL_DATE);
            querySql = "SELECT new_user as netNewUser, ref_date as refDate FROM dws_users "
                    + "WHERE ref_date >= ?"
                    + " AND ref_date < ?"
                    + " AND account_id = ?"
                    + " ORDER BY ref_date ASC";
            return clickhouseService.readData(querySql, startTimeStr, endTimeStr, accountId);
        }

        querySql = "SELECT new_user as netNewUser, ref_date as refDate FROM dws_users "
                + "WHERE account_id = ?"
                + " ORDER BY ref_date ASC";
        return clickhouseService.readData(querySql, accountId);

    }

    @Override
    public List<Map<String, Object>> accumulatedUserTrend(Integer filterDimension, Long accountId) {
        LocalDate endTime = LocalDate.now();
        String endTimeStr = endTime.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String querySql;
        LocalDate startdate =  FilterDimension.calculateStartTimeEnhanced(filterDimension);
        if (startdate != null) {
            String startTimeStr =startdate.format(DateTimeFormatter.ISO_LOCAL_DATE);
            querySql = "SELECT accumulated_user as accumulatedUser, ref_date as refDate FROM dws_users "
                    + "WHERE ref_date >= ?"
                    + " AND ref_date < ?"
                    + " AND account_id = ?"
                    + " ORDER BY ref_date ASC";
            return clickhouseService.readData(querySql, startTimeStr, endTimeStr, accountId);
        }

        querySql = "SELECT accumulated_user as accumulatedUser, ref_date as refDate FROM dws_users "
                + "WHERE account_id = ?"
                + " ORDER BY ref_date ASC";
        return clickhouseService.readData(querySql, accountId);
    }

    public List<Map<String, Object>> subscribeUserAfterRead(Long accountId) {
        PageHelper.startPage(1, 10);
        LambdaQueryWrapper<DwsContentData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(DwsContentData::getReadSubscribeUser);
        queryWrapper.eq(DwsContentData::getAccountId, accountId);

        List<DwsContentData> records = dwsContentDataMapper.selectList(queryWrapper);
        List<Map<String, Object>> articleList = records.stream().map(article -> {
            Map<String, Object> map = new HashMap<>();
            map.put("msgid", article.getMsgid());
            map.put("article_title", article.getTitle());
            map.put("total_follows", article.getReadSubscribeUser());
            map.put("last_stat_date", article.getLatestStatDate());
            return map;
        }).collect(Collectors.toList());
        return articleList;
    }
}
