package com.txwx.social.crm.common.chain;

public abstract class AbstractChainHandler<T extends BaseChainContext> implements IChainHandler<T> {
    /**
     * 下一个处理器：固定泛型T，和ChainBuilder完全匹配
     */
    private IChainHandler<T> next;

    private final String handlerName;

    protected AbstractChainHandler() {
        this.handlerName = this.getClass().getSimpleName();
    }

    protected AbstractChainHandler(String handlerName) {
        this.handlerName = handlerName;
    }

    @Override
    public void handle(T context) {
        if (context.shouldTerminateChain()) {
            return;
        }

        try {
            beforeHandle(context);
            if (context.shouldSkipCurrentHandler()) {
                context.resetInterrupt();
                handleNext(context);
                return;
            }

            doHandle(context);
            if (context.shouldTerminateChain()) {
                return;
            }

            afterHandle(context);
            handleNext(context);

        } catch (Exception e) {
            context.interruptWithError("处理器执行异常: " + handlerName, e);
        } finally {
            afterCompletion(context);
        }
    }

    protected void handleNext(T context) {
        if (next != null) {
            next.handle(context);
        }
    }

    // 钩子方法
    protected void beforeHandle(T context) {}
    protected abstract void doHandle(T context);
    protected void afterHandle(T context) {}
    protected void afterCompletion(T context) {}

    // setter/getter 固定泛型T，无通配符
    public void setNext(IChainHandler<T> next) {
        this.next = next;
    }

    public IChainHandler<T> getNext() {
        return next;
    }

    @Override
    public String getName() {
        return handlerName;
    }
}