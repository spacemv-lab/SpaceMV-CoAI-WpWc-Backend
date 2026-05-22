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
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.iam.dto.RegisterQuery;
import com.ruoyi.iam.dto.UpdateStatusRequest;
import com.ruoyi.iam.dto.vo.RegisterDetailVO;
import com.ruoyi.iam.dto.vo.RegisterListVO;
import com.ruoyi.iam.entity.IamUser;
import com.ruoyi.iam.entity.IamUserBackupContact;
import com.ruoyi.iam.entity.IamUserChannel;
import com.ruoyi.iam.mapper.IamUserBackupContactMapper;
import com.ruoyi.iam.mapper.IamUserChannelMapper;
import com.ruoyi.iam.mapper.IamUserMapper;
import com.ruoyi.iam.service.IAdminRegisterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 注册用户管理 Service 实现（管理员）
 * <p>
 * 底层数据从 IAM 表（iam_user / iam_user_channel / iam_user_backup_contact）派生，
 * 前端协议字段名保持不变。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminRegisterServiceImpl implements IAdminRegisterService {

    private final IamUserMapper iamUserMapper;
    private final IamUserChannelMapper iamUserChannelMapper;
    private final IamUserBackupContactMapper iamUserBackupContactMapper;

    @Override
    public TableDataInfo queryRegisterList(RegisterQuery query) {
        // 1. 处理手机/邮箱过滤条件：先查 iam_user_channel 获取 userId 集合
        Set<Long> channelUserIds = resolveChannelFilter(query);

        // 2. 构建 IamUser 查询条件
        LambdaQueryWrapper<IamUser> wrapper = buildUserQuery(query, channelUserIds);

        // 3. 分页查询（PageHelper 会自动从请求中读取 pageNum/pageSize）
        PageUtils.startPage();
        List<IamUser> users = iamUserMapper.selectList(wrapper);
        PageInfo<IamUser> pageInfo = new PageInfo<>(users);

        // 4. 批量补充通道和备用联系方式
        List<RegisterListVO> voList = enrichRegisterList(users);

        // 5. 构建分页响应
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setRows(voList);
        rspData.setTotal(pageInfo.getTotal());
        rspData.setMsg("查询成功");
        return rspData;
    }

    @Override
    public RegisterDetailVO queryRegisterDetail(Long registerId) {
        IamUser user = iamUserMapper.selectById(registerId);
        if (user == null) {
            return null;
        }

        // 查主通道
        List<IamUserChannel> channels = iamUserChannelMapper.selectList(
                new LambdaQueryWrapper<IamUserChannel>()
                        .eq(IamUserChannel::getUserId, user.getId())
                        .eq(IamUserChannel::getIsPrimary, "1")
                        .eq(IamUserChannel::getStatus, "0")
        );
        String phone = channels.stream()
                .filter(c -> "phone".equals(c.getChannelType()))
                .findFirst().map(IamUserChannel::getChannelAccount).orElse(null);
        String email = channels.stream()
                .filter(c -> "email".equals(c.getChannelType()))
                .findFirst().map(IamUserChannel::getChannelAccount).orElse(null);

        // 查备用联系方式
        IamUserBackupContact contact = iamUserBackupContactMapper.selectByUserId(user.getId());

        return RegisterDetailVO.builder()
                .registerId(user.getId())
                .userId(user.getId())
                .userName(user.getUsername())
                .bindPhone(phone)
                .bindEmail(email)
                .bakPhone(contact != null ? contact.getBakPhone() : null)
                .bakEmail(contact != null ? contact.getBakEmail() : null)
                .status(deriveRegisterStatus(user))
                .statusDesc(getRegisterStatusDesc(deriveRegisterStatus(user)))
                .registerTime(user.getCreateTime())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateStatus(UpdateStatusRequest request) {
        // registerId 即 iam_user.id（前端协议字段名不变）
        IamUser user = iamUserMapper.selectById(request.getRegisterId());
        if (user == null) {
            return AjaxResult.error("注册用户不存在");
        }

        // 不允许修改「已注销」或「注销中」用户的启用/停用状态
        if (!"0".equals(user.getDeleteStatus())) {
            return AjaxResult.error("该用户正在注销或已注销，无法修改状态");
        }

        // 更新 iam_user.status（0=正常, 1=停用）
        // 注意：前端 status 值在注册管理页面只映射 0=正常 1=停用
        user.setStatus(request.getStatus());
        user.setUpdateTime(new Date());
        iamUserMapper.updateById(user);

        log.info("注册用户状态修改成功: registerId={}, status={}, updateBy={}",
                request.getRegisterId(), request.getStatus(), request.getUpdateBy());

        return AjaxResult.success("状态修改成功");
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 根据手机/邮箱过滤条件，从 iam_user_channel 中查找匹配的 userId 集合
     *
     * @return null 表示无通道过滤条件；空集合表示无匹配用户
     */
    private Set<Long> resolveChannelFilter(RegisterQuery query) {
        boolean hasPhone = StringUtils.isNotEmpty(query.getPhonenumber());
        boolean hasEmail = StringUtils.isNotEmpty(query.getEmail());
        if (!hasPhone && !hasEmail) {
            return null;
        }

        Set<Long> result = null;

        if (hasPhone) {
            List<IamUserChannel> channels = iamUserChannelMapper.selectList(
                    new LambdaQueryWrapper<IamUserChannel>()
                            .eq(IamUserChannel::getChannelType, "phone")
                            .eq(IamUserChannel::getChannelAccount, query.getPhonenumber())
                            .eq(IamUserChannel::getIsPrimary, "1")
                            .eq(IamUserChannel::getStatus, "0")
            );
            result = channels.stream()
                    .map(IamUserChannel::getUserId)
                    .collect(Collectors.toSet());
        }

        if (hasEmail) {
            List<IamUserChannel> channels = iamUserChannelMapper.selectList(
                    new LambdaQueryWrapper<IamUserChannel>()
                            .eq(IamUserChannel::getChannelType, "email")
                            .eq(IamUserChannel::getChannelAccount, query.getEmail())
                            .eq(IamUserChannel::getIsPrimary, "1")
                            .eq(IamUserChannel::getStatus, "0")
            );
            Set<Long> emailUserIds = channels.stream()
                    .map(IamUserChannel::getUserId)
                    .collect(Collectors.toSet());

            if (result == null) {
                result = emailUserIds;
            } else {
                // 同时提供了 phone 和 email → AND 逻辑
                result.retainAll(emailUserIds);
            }
        }

        return result;
    }

    /**
     * 构建 IamUser 查询条件
     */
    private LambdaQueryWrapper<IamUser> buildUserQuery(RegisterQuery query, Set<Long> channelUserIds) {
        LambdaQueryWrapper<IamUser> wrapper = new LambdaQueryWrapper<>();

        // 仅展示注册来源的用户
        wrapper.eq(IamUser::getUserFrom, "register");

        // 用户名模糊搜索
        if (StringUtils.isNotEmpty(query.getUserName())) {
            wrapper.like(IamUser::getDisplayName, query.getUserName());
        }

        // 通道过滤（手机/邮箱）
        if (channelUserIds != null) {
            if (channelUserIds.isEmpty()) {
                // 通道过滤有结果但交集为空 → 用不可能的条件保证返回空
                wrapper.eq(IamUser::getId, -1L);
            } else {
                wrapper.in(IamUser::getId, channelUserIds);
            }
        }

        // 状态过滤
        if (StringUtils.isNotEmpty(query.getStatus())) {
            applyRegisterStatusFilter(wrapper, query.getStatus());
        }

        wrapper.orderByDesc(IamUser::getCreateTime);
        return wrapper;
    }

    /**
     * 将前端注册状态映射为 IamUser 查询条件
     * <p>
     * 前端 0=正常 1=停用 2=注销中 3=已注销
     */
    private void applyRegisterStatusFilter(LambdaQueryWrapper<IamUser> wrapper, String status) {
        switch (status) {
            case "0": // 正常
                wrapper.eq(IamUser::getStatus, "0")
                       .eq(IamUser::getDeleteStatus, "0");
                break;
            case "1": // 停用
                wrapper.eq(IamUser::getStatus, "1")
                       .eq(IamUser::getDeleteStatus, "0");
                break;
            case "2": // 注销中
                wrapper.eq(IamUser::getDeleteStatus, "1");
                break;
            case "3": // 已注销
                wrapper.eq(IamUser::getDeleteStatus, "2");
                break;
            default:
                // 未知状态不额外过滤
                log.warn("未知的注册状态过滤值: {}", status);
        }
    }

    /**
     * 补充用户的通道信息（手机/邮箱）和备用联系方式
     */
    private List<RegisterListVO> enrichRegisterList(List<IamUser> users) {
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

            return RegisterListVO.builder()
                    .registerId(user.getId())
                    .userId(user.getId())
                    .userName(user.getUsername())
                    .bindPhone(phone)
                    .bindEmail(email)
                    .bakPhone(contact != null ? contact.getBakPhone() : null)
                    .bakEmail(contact != null ? contact.getBakEmail() : null)
                    .status(deriveRegisterStatus(user))
                    .registerTime(user.getCreateTime())
                    .build();
        }).toList();
    }

    /**
     * 从 IamUser 派生前端注册状态
     * <p>
     * 优先级：已注销 > 注销中 > 停用 > 正常
     */
    private String deriveRegisterStatus(IamUser user) {
        if ("2".equals(user.getDeleteStatus())) return "3"; // 已注销
        if ("1".equals(user.getDeleteStatus())) return "2"; // 注销中
        if ("1".equals(user.getStatus())) return "1";       // 停用
        return "0";                                          // 正常
    }

    /**
     * 注册状态描述
     */
    private String getRegisterStatusDesc(String status) {
        switch (status) {
            case "0": return "正常";
            case "1": return "停用";
            case "2": return "注销中";
            case "3": return "已注销";
            default: return "未知";
        }
    }
}
