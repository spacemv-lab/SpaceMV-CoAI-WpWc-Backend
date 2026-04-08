package com.txwx.social.api.client;

import com.ruoyi.common.core.web.domain.AjaxResult;
import com.txwx.social.api.domain.dto.ChannelDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 渠道服务远程调用客户端接口
 *
 * @author txwx
 * @date 2026-04-04
 */
@FeignClient(
    name = "txwx-social-crm",
    path = "/channel"
)
public interface ChannelApiClient {

    /**
     * 查询渠道列表
     */
    @PostMapping("/list")
    AjaxResult getChannelList(@RequestBody ChannelDTO query);

    /**
     * 查询渠道列表
     */
    @PostMapping("/list/simple")
    AjaxResult getSimpleChannelList(@RequestBody ChannelDTO query);

    /**
     * 获取渠道详细信息
     */
    @GetMapping("/{id}")
    AjaxResult getChannelById(@PathVariable("id") Long id);

    /**
     * 新增渠道
     */
    @PostMapping
    AjaxResult addChannel(@RequestBody ChannelDTO channel);

    /**
     * 修改渠道
     */
    @PutMapping
    AjaxResult updateChannel(@RequestBody ChannelDTO channel);

    /**
     * 删除渠道
     */
    @DeleteMapping("/{ids}")
    AjaxResult deleteChannelByIds(@PathVariable("ids") String ids);
}
