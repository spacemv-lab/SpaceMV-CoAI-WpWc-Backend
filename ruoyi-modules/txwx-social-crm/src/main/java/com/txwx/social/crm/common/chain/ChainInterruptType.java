package com.txwx.social.crm.common.chain;

public enum ChainInterruptType {
    /** 无中断，继续执行 */
    NONE,
    /** 错误中断：发生业务错误/异常，终止执行并回滚事务 */
    ERROR,
    /** 正常中断：业务逻辑提前完成，终止执行不回滚 */
    NORMAL,
    /** 跳过当前：跳过当前处理器，继续执行下一个 */
    SKIP_CURRENT,
    /** 跳过剩余：跳过所有后续处理器，终止执行不回滚 */
    SKIP_REMAINING
}
