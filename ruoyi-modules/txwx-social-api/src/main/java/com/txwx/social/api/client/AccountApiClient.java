package com.txwx.social.api.client;

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
    path = "/account"
)
public interface AccountApiClient {

    /**
     * 查询账号列表
     */
    @PostMapping("/list")
    AjaxResult getAccountList(@Parameter(description = "账号信息", required = false) @RequestBody AccountDTO query);

    /**
     * 获取账号详细信息
     */
    @GetMapping("/{id}")
    AjaxResult getAccountById(@PathVariable("id") Long id);

    /**
     * 新增账号
     */
    @PostMapping
    AjaxResult addAccount(@RequestBody AccountDTO account);

    /**
     * 修改账号
     */
    @PutMapping
    AjaxResult updateAccount(@RequestBody AccountDTO account);

    /**
     * 删除账号
     */
    @DeleteMapping("/{ids}")
    AjaxResult deleteAccountByIds(@PathVariable("ids") String ids);

    /**
     * 根据产品ID查询账号列表
     */
    @GetMapping("/byProduct/{productId}")
    AjaxResult getAccountByProductId(@PathVariable("productId") Long productId);

    /**
     * 根据渠道ID查询账号列表
     */
    @GetMapping("/byChannel/{channelId}")
    AjaxResult getAccountByChannelId(@PathVariable("channelId") Long channelId);
}
