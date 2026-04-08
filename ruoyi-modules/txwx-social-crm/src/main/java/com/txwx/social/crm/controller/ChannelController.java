package com.txwx.social.crm.controller;

import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.domain.BaseEntity;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.social.api.client.ChannelApiClient;
import com.txwx.social.api.domain.dto.AccountDTO;
import com.txwx.social.api.domain.dto.ChannelDTO;
import com.txwx.social.crm.domain.po.TxwxAccountPO;
import com.txwx.social.crm.domain.po.TxwxChannelPO;
import com.txwx.social.crm.domain.po.TxwxProductChannelPO;
import com.txwx.social.crm.service.IAccountService;
import com.txwx.social.crm.service.IChannelService;
import com.txwx.social.crm.service.IProductChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 渠道控制器（实现 ChannelApiClient 接口，提供远程调用能力）
 *
 * @author txwx
 * @date 2026-04-04
 */
@RestController
@RequestMapping("/channel")
@Tag(name = "04--【CRM】--渠道管理")
public class ChannelController extends BaseController implements ChannelApiClient {

    @Autowired
    private IChannelService channelService;

    @Autowired
    private IProductChannelService productChannelService;

    @Autowired
    private IAccountService accountService;

    /* ========== 以下是 ChannelApiClient 接口的实现 ========== */

    @Override
    @PostMapping("/list")
    @Operation(summary = "查询渠道列表（client调用）")
    public AjaxResult getChannelList(@Parameter(description = "查询条件") @RequestBody(required = false) ChannelDTO query) {
        TxwxChannelPO queryPO = new TxwxChannelPO();
            if (query != null) {
            BeanUtils.copyProperties(query, queryPO);
        }
        List<TxwxChannelPO> list = channelService.selectChannelList(queryPO);
        List<Long> cids = list.stream().mapToLong(TxwxChannelPO::getId).boxed().toList();
        Map<Long,List<TxwxAccountPO>> accountMap = accountService.selectAccountByChannelIds(cids);
        List<ChannelDTO> dtoList = list.stream()
                .map(txwxChannelPO -> convertPoToDtoWithAccount(txwxChannelPO, accountMap))
                .collect(Collectors.toList());
        return AjaxResult.success(dtoList);
    }

    @Override
    @PostMapping("/list/simple")
    @Operation(summary = "查询简单渠道列表（client调用）")
    public AjaxResult getSimpleChannelList(@Parameter(description = "查询条件") @RequestBody(required = false) ChannelDTO query) {
        TxwxChannelPO queryPO = new TxwxChannelPO();
        if (query != null) {
            BeanUtils.copyProperties(query, queryPO);
        }
        List<TxwxChannelPO> list = channelService.selectChannelList(queryPO);
        List<ChannelDTO> dtoList = list.stream()
                .map(this::convertPoToDto)
                .collect(Collectors.toList());
        return AjaxResult.success(dtoList);
    }

    @Override
    @GetMapping("/{id}")
    @Operation(summary = "查询渠道（client调用）")
    public AjaxResult getChannelById(@Parameter(description = "渠道ID") @PathVariable("id") Long id) {
        TxwxChannelPO po = channelService.selectChannelById(id);
        if (po == null) {
            return AjaxResult.error("渠道不存在");
        }
        return AjaxResult.success(convertPoToDto(po));
    }

    @Override
    @PostMapping
    @Operation(summary = "新增渠道（client调用）")
    public AjaxResult addChannel(@Parameter(description = "渠道信息") @RequestBody ChannelDTO channel) {
        TxwxChannelPO po = new TxwxChannelPO();
        BeanUtils.copyProperties(channel, po);
        fillBaseInfo(po);
        TxwxProductChannelPO productChannelPO = getTxwxProductChannelPO(channel);
        return toAjax(channelService.insertChannel(po, productChannelPO));
    }

    private TxwxProductChannelPO getTxwxProductChannelPO(ChannelDTO channel) {
        TxwxProductChannelPO productChannelPO = new TxwxProductChannelPO();
        productChannelPO.setChannelId(channel.getId());
        productChannelPO.setProductId(channel.getProductId());
        fillBaseInfo(productChannelPO);
        return productChannelPO;
    }

    @Override
    @PutMapping
    @Operation(summary = "更新渠道,本接口只更新渠道基本信息，如需更改渠道和产品关系请使用ProductChannel相关接口（client调用）")
    public AjaxResult updateChannel(@Parameter(description = "渠道信息") @RequestBody ChannelDTO channel) {
        TxwxChannelPO po = new TxwxChannelPO();
        BeanUtils.copyProperties(channel, po);
        fillBaseInfo(po);
        return toAjax(channelService.updateChannel(po));
    }

    @Override
    @DeleteMapping("/{ids}")
    @Operation(summary = "批量删除渠道（client调用）")
    public AjaxResult deleteChannelByIds(@Parameter(description = "渠道ID数组") @PathVariable("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(Long::valueOf)
                .toList();

        if (idList.isEmpty()) {
            return AjaxResult.error("请提供要删除的ID列表");
        }
        return toAjax(channelService.deleteChannelByIds(idList));
    }

    /* ========== 内部转换方法 ========== */

    /**
     * 将内部 PO 对象转换为 API DTO 对象
     */
    private ChannelDTO convertPoToDto(TxwxChannelPO po) {
        if (po == null) {
            return null;
        }
        ChannelDTO dto = new ChannelDTO();
        BeanUtils.copyProperties(po, dto);
        return dto;
    }

    private ChannelDTO convertPoToDtoWithAccount(TxwxChannelPO po, Map<Long, List<TxwxAccountPO>> map) {
        if (po == null) {
            return null;
        }
        ChannelDTO dto = new ChannelDTO();
        BeanUtils.copyProperties(po, dto);
        List<TxwxAccountPO> accountPOList = map.getOrDefault(po.getId(), Lists.newArrayList());
        List<AccountDTO> accountDTOList = accountPOList.stream().map(tpo -> {
            AccountDTO accountDTO = new AccountDTO();
            BeanUtils.copyProperties(tpo, accountDTO);
            return accountDTO;
        }).toList();
        dto.setAccountDTOList(accountDTOList);
        return dto;
    }

    private void fillBaseInfo(BaseEntity po) {
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
}
