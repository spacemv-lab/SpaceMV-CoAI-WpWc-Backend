/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.common.chain;

import java.util.ArrayList;
import java.util.List;

public class ChainBuilder<T extends BaseChainContext> {
    /**
     * 全程使用固定泛型T，彻底消除通配符，无capture问题
     */
    private final List<IChainHandler<T>> handlers = new ArrayList<>();

    /**
     * 核心方法：兼容所有能处理T的处理器
     * 1. 业务专属处理器：IChainHandler<T> 直接添加
     * 2. 全局通用处理器：IChainHandler<? super T> 安全强转后添加
     *    能处理T父类的处理器，必然能处理T本身，100%类型安全
     */
    @SuppressWarnings("unchecked")
    public ChainBuilder<T> addHandler(IChainHandler<? super T> handler) {
        handlers.add((IChainHandler<T>) handler);
        return this;
    }

    /**
     * 构建责任链：无任何通配符，无编译报错
     */
    public IChainHandler<T> build() {
        if (handlers.isEmpty()) {
            throw new IllegalStateException("责任链至少需要一个处理器");
        }
        // 链式连接，全程固定泛型T，无类型不匹配问题
        for (int i = 0; i < handlers.size() - 1; i++) {
            ((AbstractChainHandler<T>) handlers.get(i)).setNext(handlers.get(i + 1));
        }
        return handlers.get(0);
    }

    /**
     * 执行责任链
     */
    public void execute(T context) {
        build().handle(context);
    }
}