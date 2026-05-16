/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.system.api.RemoteUserService;
import com.txwx.social.crm.domain.po.TxwxUserLogoutPO;
import com.txwx.social.crm.domain.po.TxwxUserRegisterPO;
import com.txwx.social.crm.domain.query.LogoutQuery;
import com.txwx.social.crm.service.ITxwxUserLogoutService;
import com.txwx.social.crm.service.ITxwxUserRegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * 注销管理控制器（管理员接口）
 *
 * @author txwx
 * @date 2026-04-20
 */
@RestController
@RequestMapping("/admin/logout")
@RequiredArgsConstructor
@Validated
@Tag(name = "注销管理（管理员）")
@Slf4j
public class UserLogoutAdminController extends BaseController {

    @Autowired
    private ITxwxUserLogoutService userLogoutService;

    @Autowired
    private ITxwxUserRegisterService userRegisterService;

    @Autowired
    private RemoteUserService remoteUserService;

    /**
     * 查询注销申请列表（分页）
     *
     * @param query 查询参数
     * @return 结果
     */
    /**
     * 注销列表 VO（关联注册信息）
     */
    @Data
    public static class LogoutListVO {
        @Schema(description = "注销ID")
        private Long logoutId;
        @Schema(description = "用户ID")
        private Long userId;
        @Schema(description = "用户名")
        private String userName;
        @Schema(description = "绑定手机")
        private String bindPhone;
        @Schema(description = "绑定邮箱")
        private String bindEmail;
        @Schema(description = "备份手机")
        private String bakPhone;
        @Schema(description = "备份邮箱")
        private String bakEmail;
        @Schema(description = "申请时间")
        private Date applyTime;
        @Schema(description = "冷却结束时间")
        private Date coolEndTime;
        @Schema(description = "状态：0=冷却中 1=已注销 2=已撤销")
        private String status;
    }

    @PostMapping("/list")
    @PreAuthorize("@ss.hasPermi('system:registerUser:list')")
    @Operation(summary = "查询注销申请列表")
    public TableDataInfo list(@Valid @RequestBody LogoutQuery query) {
        LambdaQueryWrapper<TxwxUserLogoutPO> wrapper = new LambdaQueryWrapper<>();

        if (query.getUserId() != null) {
            wrapper.eq(TxwxUserLogoutPO::getUserId, query.getUserId());
        }
        if (StringUtils.isNotEmpty(query.getStatus())) {
            wrapper.eq(TxwxUserLogoutPO::getStatus, query.getStatus());
        }

        wrapper.orderByDesc(TxwxUserLogoutPO::getApplyTime);
        startPage();
        List<TxwxUserLogoutPO> list = userLogoutService.list(wrapper);

        // 关联注册信息，补充 bindPhone / bindEmail
        List<LogoutListVO> voList = list.stream().map(logout -> {
            LogoutListVO vo = new LogoutListVO();
            vo.setLogoutId(logout.getLogoutId());
            vo.setUserId(logout.getUserId());
            vo.setApplyTime(logout.getApplyTime());
            vo.setCoolEndTime(logout.getCoolEndTime());
            vo.setStatus(logout.getStatus());

            // 查询注册扩展表
            LambdaQueryWrapper<TxwxUserRegisterPO> regWrapper = new LambdaQueryWrapper<>();
            regWrapper.eq(TxwxUserRegisterPO::getUserId, logout.getUserId())
                      .eq(TxwxUserRegisterPO::getDelFlag, "0");
            TxwxUserRegisterPO reg = userRegisterService.getOne(regWrapper);
            if (reg != null) {
                vo.setUserName(reg.getUserName());
                vo.setBindPhone(reg.getBindPhone());
                vo.setBindEmail(reg.getBindEmail());
                vo.setBakPhone(reg.getBakPhone());
                vo.setBakEmail(reg.getBakEmail());
            }
            return vo;
        }).toList();

        // 构建分页结果
        TableDataInfo tableData = new TableDataInfo();
        tableData.setRows(voList);
        tableData.setTotal(list.size());
        tableData.setCode(200);
        tableData.setMsg("查询成功");
        return tableData;
    }

    /**
     * 查询注销申请详情
     *
     * @param logoutId 注销ID
     * @return 结果
     */
    @GetMapping("/detail/{logoutId}")
    @PreAuthorize("@ss.hasPermi('system:registerUser:list')")
    @Operation(summary = "查询注销申请详情")
    public R<Map<String, Object>> detail(@NotNull(message = "注销ID不能为空") @PathVariable Long logoutId) {
        TxwxUserLogoutPO logout = userLogoutService.getById(logoutId);
        if (logout == null) {
            return R.fail("注销申请不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("logoutId", logout.getLogoutId());
        result.put("userId", logout.getUserId());
        result.put("applyTime", logout.getApplyTime());
        result.put("coolEndTime", logout.getCoolEndTime());
        result.put("status", logout.getStatus());
        result.put("statusDesc", getStatusDesc(logout.getStatus()));

        return R.ok(result);
    }



    /**
     * 获取状态描述
     */
    private String getStatusDesc(String status) {
        switch (status) {
            case "0":
                return "冷却中";
            case "1":
                return "已注销";
            case "2":
                return "已撤销";
            default:
                return "未知";
        }
    }
}
