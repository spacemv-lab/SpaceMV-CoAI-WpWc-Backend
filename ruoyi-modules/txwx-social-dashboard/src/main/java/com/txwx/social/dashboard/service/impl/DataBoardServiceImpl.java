package com.txwx.social.dashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.clickhouse.service.ClickhouseService;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.social.dashboard.domain.entity.DwsContentData;
import com.txwx.social.dashboard.domain.entity.DwsUsers;
import com.txwx.social.dashboard.domain.entity.OdsArticleDetailDaily;
import com.txwx.social.dashboard.domain.entity.mysql.UserPlatform;
import com.txwx.social.dashboard.domain.enumType.FilterDimension;
import com.txwx.social.dashboard.domain.vo.UserTotalVo;
import com.txwx.social.dashboard.mapper.DwsBizsummaryChannelDailyMapper;
import com.txwx.social.dashboard.mapper.DwsContentDataMapper;
import com.txwx.social.dashboard.mapper.OdsArticleDetailDailyMapper;
import com.txwx.social.dashboard.mapper.UserPlatformMapper;
import com.txwx.social.dashboard.service.IDataBoardService;
import com.txwx.social.dashboard.service.IDwsUsersService;
import com.txwx.social.dashboard.service.IOdsArticleDetailDailyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.rmi.ServerException;
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
    @Autowired
    private UserPlatformMapper userPlatformMapper;

    @Override
    public List<UserTotalVo> selectUserTotal(Long productId, Long platformId) {
        List<UserTotalVo> res = new ArrayList<>();
        // 获取总用户数
        LambdaQueryWrapper<DwsUsers> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(DwsUsers::getRefDate);
        // 增加平台控制
        queryWrapper.eq(DwsUsers::getProductId, productId);
        queryWrapper.eq(DwsUsers::getPlatformId, platformId);
        List<DwsUsers> list = dwsUsersService.list(queryWrapper);
        UserTotalVo userTotalVo = new UserTotalVo();
        userTotalVo.setDesc("总用户数");
        userTotalVo.setValue(list.get(0).getAccumulatedUser());
        res.add(userTotalVo);
        Map<String, Long> map = dwsBizsummaryChannelDailyMapper.selectTotalReadShare(platformId, productId);
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
    public List<List<Map<String, Object>>> readFlowTrend(Integer filterDimension, Long productId, Long platformId) {
        StringBuilder readSql = new StringBuilder();
        readSql.append("SELECT ref_date, read_user_cnt, share_user ")
                .append("FROM dws_bizsummary_channel_daily ")
                .append("WHERE channel = '全部'")
                .append(" and platform_id = " )
                .append(platformId)
                .append(" and product_id = ")
                .append(productId);
        if (filterDimension != null && filterDimension != FilterDimension.ALL.getCode()){
            readSql.append(" and ref_date >= ");
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

        List<Map<String, Object>> temp = dwsBizsummaryChannelDailyMapper.selectDataBoardReadSource(platformId, productId);
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
    public List<UserTotalVo> optimumReaderShareNum(Long productId, Long platformId) {
        String readSql = "SELECT MAX(read_user_total) AS max_read_user_total, MAX(share_user) AS max_share_user from dws_content_data where platform_id = " + platformId + " and product_id = " + productId;
        List<Map<String, Object>> list = clickhouseService.readData(readSql);
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
    public List<Map<String, Object>> netUserTrend(Integer filterDimension, Long productId, Long platformId) {
        LocalDate endTime = LocalDate.now();
        LocalDate startTime = null;
        String querySql = null;
        if (filterDimension == FilterDimension.WEEK.getCode()) {
            startTime = endTime.minusWeeks(1);
            querySql = "SELECT new_user as netNewUser, ref_date as refDate FROM dws_users "
                    + "WHERE ref_date >= '"  + startTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    + "' AND ref_date < '" + endTime.format(DateTimeFormatter.ISO_LOCAL_DATE) + "'"
                    + " AND platform_id = '" + platformId + "'"
                    + " AND product_id = '" + productId + "'"
                    + " ORDER BY ref_date ASC";
        }else if (filterDimension == FilterDimension.MONTH.getCode()) {
            startTime = endTime.minusMonths(1);
            querySql = "SELECT new_user as netNewUser, ref_date as refDate FROM dws_users "
                    + "WHERE ref_date >= '"  + startTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    + "' AND ref_date < '" + endTime.format(DateTimeFormatter.ISO_LOCAL_DATE) + "'"
                    + " AND platform_id = '" + platformId + "'"
                    + " AND product_id = '" + productId + "'"
                    + " ORDER BY ref_date ASC";
        }else if (filterDimension == FilterDimension.HALF_YEAR.getCode()) {
            startTime = endTime.minusMonths(6);
            querySql = "SELECT new_user as netNewUser, ref_date as refDate FROM dws_users "
                    + "WHERE ref_date >= '"  + startTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    + "' AND ref_date < '" + endTime.format(DateTimeFormatter.ISO_LOCAL_DATE) + "'"
                    + " AND platform_id = '" + platformId + "'"
                    + " AND product_id = '" + productId + "'"
                    + " ORDER BY ref_date ASC";
        }else if (filterDimension == FilterDimension.YEAR.getCode()){
            startTime = endTime.minusYears(1);
            querySql = "SELECT new_user as netNewUser, ref_date as refDate FROM dws_users "
                    + "WHERE ref_date >= '"  + startTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    + "' AND ref_date < '" + endTime.format(DateTimeFormatter.ISO_LOCAL_DATE) + "'"
                    + " AND platform_id = '" + platformId + "'"
                    + " AND product_id = '" + productId + "'"
                    + " ORDER BY ref_date ASC";
        }else {
            querySql = "SELECT new_user as netNewUser, ref_date as refDate FROM dws_users "
                    + " where platform_id = '" + platformId + "'"
                    + " AND product_id = '" + productId + "'"
                    + " ORDER BY ref_date ASC";
        }

        List<Map<String, Object>> list = clickhouseService.readData(querySql);
        return list;
    }

    @Override
    public List<Map<String, Object>> accumulatedUserTrend(Integer filterDimension, Long productId, Long platformId) {
        LocalDate endTime = LocalDate.now();
        LocalDate startTime = null;
        String querySql = null;
        if (filterDimension == FilterDimension.WEEK.getCode()) {
            startTime = endTime.minusWeeks(1);
            querySql = "SELECT accumulated_user as accumulatedUser, ref_date as refDate FROM dws_users "
                    + "WHERE ref_date >= '"  + startTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    + "' AND ref_date < '" + endTime.format(DateTimeFormatter.ISO_LOCAL_DATE) + "'"
                    + " AND platform_id = '" + platformId + "'"
                    + " AND product_id = '" + productId + "'"
                    + " ORDER BY ref_date ASC";
        }else if (filterDimension == FilterDimension.MONTH.getCode()) {
            startTime = endTime.minusMonths(1);
            querySql = "SELECT accumulated_user as accumulatedUser, ref_date as refDate FROM dws_users "
                    + "WHERE ref_date >= '"  + startTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    + "' AND ref_date < '" + endTime.format(DateTimeFormatter.ISO_LOCAL_DATE) + "'"
                    + " AND platform_id = '" + platformId + "'"
                    + " AND product_id = '" + productId + "'"
                    + " ORDER BY ref_date ASC";
        }else if (filterDimension == FilterDimension.HALF_YEAR.getCode()) {
            startTime = endTime.minusMonths(6);
            querySql = "SELECT accumulated_user as accumulatedUser, ref_date as refDate FROM dws_users "
                    + "WHERE ref_date >= '"  + startTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    + "' AND ref_date < '" + endTime.format(DateTimeFormatter.ISO_LOCAL_DATE) + "'"
                    + " AND platform_id = '" + platformId + "'"
                    + " AND product_id = '" + productId + "'"
                    + " ORDER BY ref_date ASC";
        }else if (filterDimension == FilterDimension.YEAR.getCode()){
            startTime = endTime.minusYears(1);
            querySql = "SELECT accumulated_user as accumulatedUser, ref_date as refDate FROM dws_users "
                    + "WHERE ref_date >= '"  + startTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    + "' AND ref_date < '" + endTime.format(DateTimeFormatter.ISO_LOCAL_DATE) + "'"
                    + " AND platform_id = '" + platformId + "'"
                    + " AND product_id = '" + productId + "'"
                    + " ORDER BY ref_date ASC";
        }else {
            querySql = "SELECT accumulated_user as accumulatedUser, ref_date as refDate FROM dws_users "
                    + " where platform_id = '" + platformId + "'"
                    + " AND product_id = '" + productId + "'"
                    + " ORDER BY ref_date ASC";
        }

        List<Map<String, Object>> list = clickhouseService.readData(querySql);
        return list;
    }

    public List<Map<String, Object>> subscribeUserAfterRead(Long productId, Long platformId) {
        PageHelper.startPage(1, 10);
        LambdaQueryWrapper<DwsContentData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(DwsContentData::getReadSubscribeUser);
        queryWrapper.eq(DwsContentData::getPlatformId, platformId);
        queryWrapper.eq(DwsContentData::getProductId, productId);
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
