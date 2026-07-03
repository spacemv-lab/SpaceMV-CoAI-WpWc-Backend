package com.ruoyi.iam.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.iam.dto.WhitelistCheckRequest;
import com.ruoyi.iam.dto.WhitelistCheckResponse;
import com.ruoyi.iam.service.RegisterValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 白名单校验控制器
 * <p>
 * 提供白名单预检接口，前端可调用此接口判断是否允许注册。
 * 内嵌限流保护，防止恶意调用。
 *
 * @author txwx
 */
@RestController
@RequestMapping("/auth/v1/register/whitelist")
@Slf4j
@Tag(name = "白名单校验")
public class RegisterWhitelistController {

    private final RegisterValidator validator;

    // 单 JVM 限流 50 req/s
    private final RateLimiter rateLimiter = RateLimiter.create(50.0);

    public RegisterWhitelistController(RegisterValidator validator) {
        this.validator = validator;
    }

    /**
     * 白名单校验接口
     * <p>
     * 限流策略：返回 200 + RATE_LIMITED reason（而非 429）
     *
     * @param req 校验请求
     * @return 校验结果
     */
    @PostMapping("/check")
    @Operation(summary = "白名单校验（预检）")
    public R<WhitelistCheckResponse> check(@RequestBody WhitelistCheckRequest req) {
        // 限流检查
        if (!rateLimiter.tryAcquire()) {
            log.warn("白名单查询限流: account={}, productLine={}", req.getAccount(), req.getProductLine());
            WhitelistCheckResponse resp = new WhitelistCheckResponse();
            resp.setAllowed(false);
            resp.setReason("RATE_LIMITED");
            resp.setProductLine(req.getProductLine());
            resp.setMessage("请求过于频繁，请稍后重试");
            return R.ok(resp);
        }

        // 白名单校验
        com.ruoyi.iam.dto.WhitelistCheckResult result = validator.canRegister(req.getAccount(), req.getProductLine());

        WhitelistCheckResponse resp = new WhitelistCheckResponse();
        resp.setAllowed(result.isAllowed());
        resp.setReason(result.getReason());
        resp.setProductLine(req.getProductLine());
        resp.setMessage(result.isAllowed() ? "可以通过注册" : getReasonMessage(result.getReason()));

        return R.ok(resp);
    }

    /**
     * 根据 reason 获取前端提示信息
     */
    private String getReasonMessage(String reason) {
        if ("DENIED".equals(reason)) {
            return "当前账号不在注册白名单中";
        } else if ("MISSING_PRODUCT_LINE".equals(reason)) {
            return "请指定产品线";
        } else if ("CONFIG_DISABLED".equals(reason)) {
            return "白名单校验未开启";
        } else if ("CONFIG_ABNORMAL".equals(reason)) {
            return "配置异常，默认放行";
        }
        return "";
    }
}
