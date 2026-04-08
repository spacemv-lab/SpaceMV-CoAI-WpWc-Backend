package com.txwx.social.crm.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Base64编解码工具类
 * 用于处理HTML内容的编码和解码
 *
 * @author txwx
 * @date 2025-01-16
 */
public class Base64Util {

    /**
     * 将字符串编码为Base64
     *
     * @param content 原始字符串
     * @return Base64编码后的字符串
     */
    public static String encode(String content) {
        if (content == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 将Base64字符串解码为原始字符串
     *
     * @param encoded Base64编码的字符串
     * @return 解码后的原始字符串
     */
    public static String decode(String encoded) {
        if (encoded == null) {
            return null;
        }
        byte[] decodedBytes = Base64.getDecoder().decode(encoded);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    /**
     * 判断字符串是否是Base64编码格式
     *
     * @param content 待检查的字符串
     * @return true 如果是Base64编码，否则返回false
     */
    public static boolean isBase64(String content) {
        if (content == null || content.isEmpty()) {
            return false;
        }
        try {
            Base64.getDecoder().decode(content);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
