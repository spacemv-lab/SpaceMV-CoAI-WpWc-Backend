package com.ruoyi.iam.aspect;

import com.ruoyi.common.core.constant.CacheConstants;
import com.ruoyi.common.core.exception.CaptchaException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.iam.dto.CaptchaRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 图形验证码校验切面
 * <p>
 * 拦截所有标注了 {@code @CaptchaValidate} 的 Controller 方法，
 * 从方法参数中查找实现了 {@code CaptchaRequest} 接口的 DTO，
 * 提取 code 和 uuid 并调用 Redis 校验图形验证码。
 * <p>
 * 校验逻辑与 Gateway 层 {@code ValidateCodeFilter} 一致：
 * <ol>
 *   <li>从 Redis 查询 {@code captcha_codes:{uuid}} 的值</li>
 *   <li>不存在 → 验证码已失效</li>
 *   <li>存在但不匹配 → 验证码错误</li>
 *   <li>匹配 → 删除该 key（一次性使用），放行请求</li>
 * </ol>
 *
 * @author txwx
 * @see com.ruoyi.iam.annotation.CaptchaValidate
 * @see CaptchaRequest
 */
@Aspect
@Component
public class CaptchaValidateAspect
{
    @Autowired
    private RedisService redisService;

    @Around("@annotation(com.ruoyi.iam.annotation.CaptchaValidate)")
    public Object around(ProceedingJoinPoint point) throws Throwable
    {
        CaptchaRequest captchaReq = findCaptchaRequest(point);
        if (captchaReq == null)
        {
            throw new CaptchaException("缺少验证码参数");
        }

        validate(captchaReq.getCode(), captchaReq.getUuid());

        return point.proceed();
    }

    /**
     * 从方法参数中查找第一个实现了 {@code CaptchaRequest} 接口的参数
     */
    private CaptchaRequest findCaptchaRequest(ProceedingJoinPoint point)
    {
        for (Object arg : point.getArgs())
        {
            if (arg instanceof CaptchaRequest)
            {
                return (CaptchaRequest) arg;
            }
        }
        return null;
    }

    /**
     * 校验图形验证码（与 Gateway ValidateCodeServiceImpl.checkCaptcha 逻辑一致）
     */
    private void validate(String code, String uuid)
    {
        // 1. 空值校验
        if (StringUtils.isEmpty(code))
        {
            throw new CaptchaException("验证码不能为空");
        }

        // 2. 从 Redis 获取存储的验证码
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
        String captcha = redisService.getCacheObject(verifyKey);

        // 3. 不存在 → 已失效
        if (captcha == null)
        {
            throw new CaptchaException("验证码已失效");
        }

        // 4. 一次性使用，校验后立即删除（防止重放）
        redisService.deleteObject(verifyKey);

        // 5. 内容匹配（忽略大小写）
        if (!code.equalsIgnoreCase(captcha))
        {
            throw new CaptchaException("验证码错误");
        }
    }
}
