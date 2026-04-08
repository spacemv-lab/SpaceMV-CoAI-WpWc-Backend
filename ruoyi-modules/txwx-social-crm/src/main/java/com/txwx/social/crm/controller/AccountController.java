package com.txwx.social.crm.controller;

import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.social.api.client.AccountApiClient;
import com.txwx.social.api.domain.dto.AccountDTO;
import com.txwx.social.crm.domain.po.TxwxAccountPO;
import com.txwx.social.crm.service.IAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 账号控制器（实现 AccountApiClient 接口，提供远程调用能力）
 *
 * @author txwx
 * @date 2026-04-04
 */
@RestController
@RequestMapping("/account")
@Tag(name = "05--【CRM】--账号管理")
public class AccountController extends BaseController implements AccountApiClient {

    @Autowired
    private IAccountService accountService;

    /* ========== 以下是 AccountApiClient 接口的实现 ========== */

    @Override
    @PostMapping("/list")
    @Operation(summary = "获取账号列表")
    public AjaxResult getAccountList(@Parameter(description = "查询条件") @RequestBody(required = false) AccountDTO query) {
        TxwxAccountPO queryPO = new TxwxAccountPO();
        if (query != null) {
            BeanUtils.copyProperties(query, queryPO);
        }
        List<TxwxAccountPO> list = accountService.selectAccountList(queryPO);
        List<AccountDTO> dtoList = list.stream()
                .map(this::convertPoToDto)
                .collect(Collectors.toList());
        return AjaxResult.success(dtoList);
    }

    @Override
    @GetMapping("/{id}")
    @Operation(summary = "按id获取账号")
    public AjaxResult getAccountById(@Parameter(description = "账号ID") @PathVariable("id") Long id) {
        TxwxAccountPO po = accountService.selectAccountById(id);
        if (po == null) {
            return AjaxResult.error("账号不存在");
        }
        return AjaxResult.success(convertPoToDto(po));
    }

    @Override
    @PostMapping
    @Operation(summary = "添加账号")
    public AjaxResult addAccount(@Parameter(description = "账号信息") @RequestBody AccountDTO account) {
        TxwxAccountPO po = new TxwxAccountPO();
        BeanUtils.copyProperties(account, po);
        fillBaseInfo(po);
        return toAjax(accountService.insertAccount(po));
    }

    private void fillBaseInfo(TxwxAccountPO po) {
        //TODO 测试用
        String operator = SecurityUtils.getUsername();
        if (StringUtils.isEmpty(operator)) {
            operator = "管理员";
        }
        po.setCreateBy(operator);
        po.setUpdateBy(operator);
        po.setCreateTime(new Date());
        po.setUpdateTime(new Date());
    }

    @Override
    @PutMapping
    @Operation(summary = "更新账号")
    public AjaxResult updateAccount(@Parameter(description = "账号信息") @RequestBody AccountDTO account) {
        TxwxAccountPO po = new TxwxAccountPO();
        BeanUtils.copyProperties(account, po);
        fillBaseInfo(po);
        return toAjax(accountService.updateAccount(po));
    }

    @Override
    @DeleteMapping("/{ids}")
    @Operation(summary = "批量删除账号")
    public AjaxResult deleteAccountByIds(@Parameter(description = "账号ID数组") @PathVariable("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(Long::valueOf)
                .toList();

        if (idList.isEmpty()) {
            return AjaxResult.error("请提供要删除的ID列表");
        }
        return toAjax(accountService.deleteAccountByIds(idList));
    }

    @Override
    @GetMapping("/byProduct/{productId}")
    @Operation(summary = "通过产品id批量查询账号列表")
    public AjaxResult getAccountByProductId(@Parameter(description = "产品ID") @PathVariable("productId") Long productId) {
        List<TxwxAccountPO> list = accountService.selectAccountByProductId(productId);
        List<AccountDTO> dtoList = list.stream()
                .map(this::convertPoToDto)
                .collect(Collectors.toList());
        return AjaxResult.success(dtoList);
    }

    @Override
    @GetMapping("/byChannel/{channelId}")
    @Operation(summary = "通过渠道id批量查询账号列表")
    public AjaxResult getAccountByChannelId(@Parameter(description = "渠道ID") @PathVariable("channelId") Long channelId) {
        List<TxwxAccountPO> list = accountService.selectAccountByChannelId(channelId);
        List<AccountDTO> dtoList = list.stream()
                .map(this::convertPoToDto)
                .collect(Collectors.toList());
        return AjaxResult.success(dtoList);
    }

    /* ========== 内部转换方法 ========== */

    /**
     * 将内部 PO 对象转换为 API DTO 对象
     */
    private AccountDTO convertPoToDto(TxwxAccountPO po) {
        if (po == null) {
            return null;
        }
        AccountDTO dto = new AccountDTO();
        BeanUtils.copyProperties(po, dto);
        return dto;
    }
}
