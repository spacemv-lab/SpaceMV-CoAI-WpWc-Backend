package com.txwx.social.crm.common.chain;


public interface IChainHandler<T extends BaseChainContext> {
    void handle(T context);
    default String getName() { return this.getClass().getSimpleName(); }
}
