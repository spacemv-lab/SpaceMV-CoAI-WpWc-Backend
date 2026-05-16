/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.common.chain;


public interface IChainHandler<T extends BaseChainContext> {
    void handle(T context);
    default String getName() { return this.getClass().getSimpleName(); }
}
