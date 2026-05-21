package com.ruoyi.iam.filter;

/**
 * Token 校验器接口 — 插件化架构
 * <p>
 * 每个业务系统可提供自己的 TokenValidator 实现，
 * IamTokenValidationFilter 按 order 顺序遍历所有已注册的 Validator，
 * 任一成功即视为认证通过。
 *
 * @author txwx
 */
public interface TokenValidator {

    /**
     * 校验并解析 token，返回 IAM 用户 ID
     *
     * @param token 原始 JWT token（不含 Bearer 前缀）
     * @return IAM 用户 ID（iam_user.id），解析失败返回 null
     */
    Long validate(String token);

    /**
     * 优先级，数字越小越优先
     */
    int getOrder();
}
