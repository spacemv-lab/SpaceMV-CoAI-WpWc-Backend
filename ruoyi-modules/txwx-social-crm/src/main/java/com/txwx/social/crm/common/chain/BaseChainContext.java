/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.common.chain;

import com.ruoyi.common.core.web.page.PageDomain;
import com.ruoyi.common.core.web.page.TableDataInfo;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public abstract class BaseChainContext {
    /** 中断类型 */
    private ChainInterruptType interruptType = ChainInterruptType.NONE;
    /** 中断信息 */
    private String interruptMessage;
    /** 中断异常（仅ERROR类型） */
    private Throwable interruptException;

    /** 扩展属性容器 */
    private final Map<String, Object> ext = new HashMap<>(16);
    /** 分页参数 */
    private PageDomain pageDomain;
    /** 分页结果 */
    private TableDataInfo pageResult;

    // 全局通用能力字段
    private String permissionCode;
    private List<String> permissionCodes;
    private boolean skipPermissionCheck = false;
    private String sentinelResourceName;
    private boolean skipSentinelRateLimit = false;

    // ==================== 核心中断控制方法 ====================
    public void interruptWithError(String message) {
        this.interruptType = ChainInterruptType.ERROR;
        this.interruptMessage = message;
    }

    public void interruptWithError(String message, Throwable e) {
        this.interruptType = ChainInterruptType.ERROR;
        this.interruptMessage = message;
        this.interruptException = e;
    }

    public void interruptNormally(String message) {
        this.interruptType = ChainInterruptType.NORMAL;
        this.interruptMessage = message;
    }

    public void skipCurrentHandler(String message) {
        this.interruptType = ChainInterruptType.SKIP_CURRENT;
        this.interruptMessage = message;
    }

    public void skipRemainingHandlers(String message) {
        this.interruptType = ChainInterruptType.SKIP_REMAINING;
        this.interruptMessage = message;
    }

    // ==================== 中断状态判断 ====================
    public boolean shouldTerminateChain() {
        return interruptType == ChainInterruptType.ERROR
                || interruptType == ChainInterruptType.NORMAL
                || interruptType == ChainInterruptType.SKIP_REMAINING;
    }

    public boolean shouldSkipCurrentHandler() {
        return interruptType == ChainInterruptType.SKIP_CURRENT;
    }

    public void resetInterrupt() {
        this.interruptType = ChainInterruptType.NONE;
        this.interruptMessage = null;
        this.interruptException = null;
    }


    // ==================== 通用工具方法 ====================
    public void putExt(String key, Object value) {
        ext.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getExt(String key, Class<T> clazz) {
        return (T) ext.get(key);
    }
}
