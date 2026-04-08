package com.txwx.social.crm.controller;

import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.social.crm.domain.po.TxwxChannelPO;
import com.txwx.social.crm.domain.po.TxwxProductChannelPO;
import com.txwx.social.crm.service.IProductChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 产品渠道关联控制器
 *
 * @author txwx
 * @date 2026-04-06
 */
@RestController
@RequestMapping("/productChannel")
@Tag(name = "07--【CRM】--产品渠道关联")
public class ProductChannelController extends BaseController {

    @Autowired
    private IProductChannelService productChannelService;

    /**
     * 查询产品渠道关联列表
     */
    @PostMapping("/list")
    @Operation(summary = "查询产品渠道关联列表")
    public AjaxResult list(@Parameter(description = "查询条件") TxwxProductChannelPO productChannel) {
        List<TxwxProductChannelPO> list = productChannelService.selectProductChannelList(productChannel);
        return AjaxResult.success(list);
    }

    /**
     * 查询产品渠道关联详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询产品渠道关联详情")
    public AjaxResult get(@Parameter(description = "关联ID") @PathVariable Long id) {
        TxwxProductChannelPO productChannel = productChannelService.selectProductChannelById(id);
        return AjaxResult.success(productChannel);
    }

    /**
     * 新增产品渠道关联
     */
    @PostMapping
    @Operation(summary = "新增产品渠道关联")
    public AjaxResult add(@Parameter(description = "关联信息") @RequestBody TxwxProductChannelPO productChannel) {
        fillBaseInfo(productChannel);
        return toAjax(productChannelService.insertProductChannel(productChannel));
    }

    /**
     * 修改产品渠道关联
     */
    @PutMapping
    @Operation(summary = "修改产品渠道关联")
    public AjaxResult update(@Parameter(description = "关联信息") @RequestBody TxwxProductChannelPO productChannel) {
        fillBaseInfo(productChannel);
        return toAjax(productChannelService.updateProductChannel(productChannel));
    }

    /**
     * 删除产品渠道关联
     */
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除产品渠道关联")
    public AjaxResult delete(@Parameter(description = "关联ID数组") @PathVariable String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(Long::valueOf)
                .toList();

        if (idList.isEmpty()) {
            return AjaxResult.error("请提供要删除的ID列表");
        }
        return toAjax(productChannelService.deleteProductChannelByChannelIds(idList));
    }

    /**
     * 根据产品ID查询渠道ID列表
     */
    @GetMapping("/channelIds/{productId}")
    @Operation(summary = "根据产品ID查询渠道ID列表")
    public AjaxResult getChannelIdsByProductId(@Parameter(description = "产品ID") @PathVariable Long productId) {
        List<Long> channelIds = productChannelService.selectChannelIdsByProductId(productId);
        return AjaxResult.success(channelIds);
    }

    /**
     * 根据渠道ID查询产品ID列表
     */
    @GetMapping("/productIds/{channelId}")
    @Operation(summary = "根据渠道ID查询产品ID列表")
    public AjaxResult getProductIdsByChannelId(@Parameter(description = "渠道ID") @PathVariable Long channelId) {
        List<Long> productIds = productChannelService.selectProductIdsByChannelId(channelId);
        return AjaxResult.success(productIds);
    }

    private void fillBaseInfo(TxwxProductChannelPO po) {
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
