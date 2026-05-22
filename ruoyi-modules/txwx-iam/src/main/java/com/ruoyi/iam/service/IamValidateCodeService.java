/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.imageio.ImageIO;

import com.ruoyi.common.core.constant.CacheConstants;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.exception.CaptchaException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.sign.Base64;
import com.ruoyi.common.core.utils.uuid.IdUtils;
import com.ruoyi.common.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;
import com.google.code.kaptcha.Producer;

/**
 * IAM 图形验证码服务
 * <p>
 * 负责生成图形验证码（GET /auth/v1/code）和校验验证码（POST /auth/v1/checkHuman）。
 * 存储到 Redis 的 key 前缀与 Gateway 层 {@code captcha_codes:} 一致，保证互操作性。
 *
 * @author txwx
 */
@Service
public class IamValidateCodeService
{
    @Resource(name = "iamCaptchaProducer")
    private Producer captchaProducer;

    @Resource(name = "iamCaptchaProducerMath")
    private Producer captchaProducerMath;

    @Autowired
    private RedisService redisService;

    /** 验证码开关（从 nacos/yml 读取 security.captcha.enabled） */
    @Value("${security.captcha.enabled:true}")
    private boolean captchaEnabled;

    /** 验证码类型（math / char） */
    @Value("${security.captcha.type:math}")
    private String captchaType;

    /**
     * 生成验证码
     *
     * @return Map 包含 captchaEnabled / uuid / img(base64)
     */
    public Map<String, Object> createCaptcha() throws IOException
    {
        Map<String, Object> result = new HashMap<>();
        result.put("captchaEnabled", captchaEnabled);

        if (!captchaEnabled)
        {
            return result;
        }

        // 生成 UUID 作为 Redis key
        String uuid = IdUtils.simpleUUID();
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + uuid;

        String capStr;
        String code;
        BufferedImage image;

        if ("math".equals(captchaType))
        {
            String capText = captchaProducerMath.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            image = captchaProducerMath.createImage(capStr);
        }
        else
        {
            capStr = code = captchaProducer.createText();
            image = captchaProducer.createImage(capStr);
        }

        // 存入 Redis（3 分钟有效期）
        redisService.setCacheObject(verifyKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);

        // 图片转 base64
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        ImageIO.write(image, "jpg", os);

        result.put("uuid", uuid);
        result.put("img", Base64.encode(os.toByteArray()));
        return result;
    }

    /**
     * 校验验证码
     *
     * @param code 用户输入的验证码
     * @param uuid 验证码 UUID
     * @throws CaptchaException 校验失败时抛出
     */
    public void checkCaptcha(String code, String uuid) throws CaptchaException
    {
        if (StringUtils.isEmpty(code))
        {
            throw new CaptchaException("验证码不能为空");
        }

        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
        String captcha = redisService.getCacheObject(verifyKey);

        if (captcha == null)
        {
            throw new CaptchaException("验证码已失效");
        }

        // 一次性使用，校验后立即删除
        redisService.deleteObject(verifyKey);

        if (!code.equalsIgnoreCase(captcha))
        {
            throw new CaptchaException("验证码错误");
        }
    }
}
