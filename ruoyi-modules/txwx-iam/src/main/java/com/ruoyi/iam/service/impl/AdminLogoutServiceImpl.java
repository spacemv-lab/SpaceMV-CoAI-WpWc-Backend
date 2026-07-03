/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.core.utils.PageUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.iam.dto.LogoutQuery;
import com.ruoyi.iam.dto.vo.LogoutDetailVO;
import com.ruoyi.iam.dto.vo.LogoutListVO;
import com.ruoyi.iam.entity.IamUser;
import com.ruoyi.iam.entity.IamUserBackupContact;
import com.ruoyi.iam.entity.IamUserChannel;
import com.ruoyi.iam.mapper.IamUserBackupContactMapper;
import com.ruoyi.iam.mapper.IamUserChannelMapper;
import com.ruoyi.iam.mapper.IamUserMapper;
import com.ruoyi.iam.service.IAdminLogoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 注销管理 Service 实现（管理员）
 * <p>
 * 数据来自 iam_user 表（deleteStatus != 0 的用户）
 * 注销状态：0=冷却中（deleteStatus=1）, 1=已注销（deleteStatus=2）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminLogoutServiceImpl implements IAdminLogoutService {

    private final IamUserMapper iamUserMapper;
    private final IamUserChannelMapper iamUserChannelMapper;
    private final IamUserBackupContactMapper iamUserBackupContactMapper;

    @Override
    public TableDataInfo queryLogoutList(LogoutQuery query) {
        // 1. 构建查询条件
        LambdaQueryWrapper<IamUser> wrapper = new LambdaQueryWrapper<>();

        // 只查有注销记录的用户
        wrapper.ne(IamUser::getDeleteStatus, "0");

        if (query.getUserId() != null) {
            wrapper.eq(IamUser::getId, query.getUserId());
        }

        // 注销状态过滤
        if (StringUtils.isNotEmpty(query.getStatus())) {
            applyLogoutStatusFilter(wrapper, query.getStatus());
        }

        wrapper.orderByDesc(IamUser::getDeleteApplyTime);

        // 2. 分页查询
        PageUtils.startPage();
        List<IamUser> users = iamUserMapper.selectList(wrapper);
        PageInfo<IamUser> pageInfo = new PageInfo<>(users);

        // 3. 批量补充通道和备用联系方式
        List<LogoutListVO> voList = enrichLogoutList(users);

        // 4. 构建响应
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setRows(voList);
        rspData.setTotal(pageInfo.getTotal());
        rspData.setMsg("查询成功");
        return rspData;
    }

    @Override
    public LogoutDetailVO queryLogoutDetail(Long logoutId) {
        IamUser user = iamUserMapper.selectById(logoutId);
        if (user == null || "0".equals(user.getDeleteStatus())) {
            return null;
        }

        String status = deriveLogoutStatus(user);
        return LogoutDetailVO.builder()
                .logoutId(user.getId())
                .userId(user.getId())
                .applyTime(user.getDeleteApplyTime())
                .coolEndTime(user.getDeleteScheduledAt())
                .status(status)
                .statusDesc(getLogoutStatusDesc(status))
                .build();
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 注销状态过滤
     * 前端：0=冷却中, 1=已注销（已撤销已忽略）
     */
    private void applyLogoutStatusFilter(LambdaQueryWrapper<IamUser> wrapper, String status) {
        if ("0".equals(status)) {
            wrapper.eq(IamUser::getDeleteStatus, "1"); // 冷却中
        } else if ("1".equals(status)) {
            wrapper.eq(IamUser::getDeleteStatus, "2"); // 已注销
        } else {
            log.warn("未知的注销状态过滤值: {}", status);
        }
    }

    /**
     * 补充用户通道信息和备用联系方式
     */
    private List<LogoutListVO> enrichLogoutList(List<IamUser> users) {
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> userIds = users.stream()
                .map(IamUser::getId)
                .collect(Collectors.toSet());

        // 批量加载主通道
        List<IamUserChannel> allChannels = iamUserChannelMapper.selectList(
                new LambdaQueryWrapper<IamUserChannel>()
                        .in(IamUserChannel::getUserId, userIds)
                        .eq(IamUserChannel::getIsPrimary, "1")
                        .eq(IamUserChannel::getStatus, "0")
        );
        Map<Long, List<IamUserChannel>> channelMap = allChannels.stream()
                .collect(Collectors.groupingBy(IamUserChannel::getUserId));

        // 批量加载备用联系方式
        List<IamUserBackupContact> allContacts = iamUserBackupContactMapper.selectList(
                new LambdaQueryWrapper<IamUserBackupContact>()
                        .in(IamUserBackupContact::getUserId, userIds)
        );
        Map<Long, IamUserBackupContact> contactMap = allContacts.stream()
                .collect(Collectors.toMap(
                        IamUserBackupContact::getUserId,
                        Function.identity(),
                        (a, b) -> a
                ));

        // 组装 VO
        return users.stream().map(user -> {
            List<IamUserChannel> userChannels = channelMap.getOrDefault(user.getId(), Collections.emptyList());
            IamUserBackupContact contact = contactMap.get(user.getId());

            String phone = userChannels.stream()
                    .filter(c -> "phone".equals(c.getChannelType()))
                    .findFirst()
                    .map(IamUserChannel::getChannelAccount)
                    .orElse(null);
            String email = userChannels.stream()
                    .filter(c -> "email".equals(c.getChannelType()))
                    .findFirst()
                    .map(IamUserChannel::getChannelAccount)
                    .orElse(null);

            return LogoutListVO.builder()
                    .logoutId(user.getId())
                    .userId(user.getId())
                    .userName(user.getUsername())
                    .bindPhone(phone)
                    .bindEmail(email)
                    .bakPhone(contact != null ? contact.getBakPhone() : null)
                    .bakEmail(contact != null ? contact.getBakEmail() : null)
                    .applyTime(user.getDeleteApplyTime())
                    .coolEndTime(user.getDeleteScheduledAt())
                    .status(deriveLogoutStatus(user))
                    .build();
        }).toList();
    }

    /**
     * 从 IamUser 派生前端注销状态
     * <p>
     * 0=冷却中（deleteStatus=1）, 1=已注销（deleteStatus=2）
     * 注：原 CRM 的「已撤销」状态不保留
     */
    private String deriveLogoutStatus(IamUser user) {
        if ("2".equals(user.getDeleteStatus())) return "1"; // 已注销
        return "0"; // 冷却中（deleteStatus=1）
    }

    /**
     * 注销状态描述
     */
    private String getLogoutStatusDesc(String status) {
        if ("0".equals(status)) return "冷却中";
        if ("1".equals(status)) return "已注销";
        return "未知";
    }
}
