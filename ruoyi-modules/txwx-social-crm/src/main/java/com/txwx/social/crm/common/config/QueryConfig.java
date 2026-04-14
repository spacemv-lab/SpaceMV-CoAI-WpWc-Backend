package com.txwx.social.crm.common.config;


import com.ruoyi.common.core.web.page.PageDomain;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 产品模块 查询/更新 开关配置包装类
 * 统一管理产品关联数据的查询、更新控制开关
 */
@Data
@ToString
public class QueryConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否查询产品基础信息 默认：false
     */
    private boolean queryProduct = false;

    /**
     * 是否查询产品渠道信息 默认：false
     */
    private boolean queryChannels = false;

    /**
     * 是否查询渠道账号信息 默认：false
     */
    private boolean queryAccount = false;

    /**
     * 是否更新产品渠道信息 默认：false
     */
    private boolean updateChannels = false;

    // ==================== 无参构造 ====================
    public QueryConfig() {
    }

    // ==================== 链式调用（推荐） ====================
    public QueryConfig queryProduct(boolean queryProduct) {
        this.queryProduct = queryProduct;
        return this;
    }

    public QueryConfig queryChannels(boolean queryChannels) {
        this.queryChannels = queryChannels;
        return this;
    }

    public QueryConfig queryAccount(boolean queryAccount) {
        this.queryAccount = queryAccount;
        return this;
    }

    public QueryConfig updateChannels(boolean updateChannels) {
        this.updateChannels = updateChannels;
        return this;
    }

}
