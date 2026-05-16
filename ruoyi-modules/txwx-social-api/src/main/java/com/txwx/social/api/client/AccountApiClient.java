/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.api.client;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.txwx.social.api.domain.dto.AccountDTO;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 账号服务远程调用客户端接口
 *
 * @author txwx
 * @date 2026-04-04
 */
@FeignClient(
    name = "txwx-social-crm",
    contextId = "accountApiClient"
)
public interface AccountApiClient {

    /**
     * 查询账号列表
     */
    @PostMapping("/account/list")
    R<List<AccountDTO>> getAccountList(@Parameter(description = "账号信息", required = false) @RequestBody AccountDTO query);

    /**
     * 获取账号详细信息
     */
    @GetMapping("/account/{id}")
    R<AccountDTO> getAccountById(@PathVariable("id") Long id);

    /**
     * 新增账号
     */
    @PostMapping
    R<Boolean> addAccount(@RequestBody AccountDTO account);

    /**
     * 修改账号
     */
    @PutMapping
    R<Boolean> updateAccount(@RequestBody AccountDTO account);

    /**
     * 删除账号
     */
    @DeleteMapping("/{ids}")
    R<Boolean> deleteAccountByIds(@PathVariable("ids") String ids);

    /**
     * 根据产品ID查询账号列表
     */
    @GetMapping("/account/byProduct/{productId}")
    R<List<AccountDTO>> getAccountByProductId(@PathVariable("productId") Long productId);

    /**
     * 根据渠道ID查询账号列表
     */
    @GetMapping("/account/byChannel/{channelId}")
    R<List<AccountDTO>> getAccountByChannelId(@PathVariable("channelId") Long channelId);
}
