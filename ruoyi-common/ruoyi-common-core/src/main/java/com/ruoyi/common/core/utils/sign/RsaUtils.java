/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.common.core.utils.sign;

import com.ruoyi.common.core.config.RsaKeyConfig;
import org.apache.commons.codec.binary.Base64;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA 加密解密（密钥从 Nacos 配置读取）
 *
 * @author ruoyi
 **/
@Component
public class RsaUtils
        implements ApplicationListener<ContextRefreshedEvent>
{
    private static ApplicationContext applicationContext;

    /**
     * 保存 Spring 上下文，用于后续获取 RsaKeyConfig Bean
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        applicationContext = event.getApplicationContext();
    }

    /**
     * 实时从 RsaKeyConfig 获取 私钥（非空兜底）
     */
    private static String getPrivateKey() {
        String key = getPrivateKeyFromConfig();
        if (key != null && !key.trim().isEmpty()) {
            return key;
        }
        // 兜底：返回内置默认私钥（与 RsaKeyConfig 一致）
        return "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAI+rBZARCpd8/FrcYVS81H7WuQxeYtKVdmp4ERiteqMLFWn5x0ogM84qOJqlbTpFK4YQFZC4rA3IbUikNEt12J9knRNlAR3cE2Roip9TPhET6qGchvjx1wlDBB2LC9N+uQsKTLrtpzOFG3yTBw68bR0rrxBUjbEqjBPgEwaSjB8DAgMBAAECgYAG5yVuYL5wCe08IzBs0fsQMk2UEELr5SRaUduMb0vTSRIl1Etxi27Byw1XzN2cxtVxBkieLZyPhJMcdU6EvUXqGQjfCCjeZxLD53Q6te3Dj7fiEnvpYB+ISrSwQ5hysbTG5UTKa2KwrFIDbXSC9vZKgjDpYig43OhWddiQ7Y3OPQJBALd7YeYtF4xRbiOnPXy4XJhE0y9VlMcD0VGCwBHcL8mRCTl35IZShQhEAgMhFd31Hyz15CdaBNkVMLtntN5KuscCQQDIc0l2tNukj/1jphP7aMvRKWNogNr/gjxzy6RC1EDDZu6JRhhUjukm0CI1sdu98/lvheIynaj1S+pnjRYioZ3lAkAoljlssjLQTj7/0gHO8fVBlY/lm5fCgjyuPC8ChGNpwhR5SuUZNW3KC0kqqgntREi2KFpkvgvufTp/agxfU8aHAkAEC+fAwLfqY4m++DxRB/WNXGOIWYmSPOPRhpvjSXuhNjO8i7C0DEqCoRL/uH5yIDm52Z8OXIZrpUOvIXb/7flNAkEAmsG8NZxapGPPWuFNgpbgi7b4Vt6pixaAv65HdXHjYLsXSS75EFHrHIU554FBnutSSJvUBUWBCgxUjv77bFalog\\u003d\\u003d";
    }

    /**
     * 实时从 RsaKeyConfig 获取 公钥（非空兜底）
     */
    private static String getPublicKey() {
        String key = getPublicKeyFromConfig();
        if (key != null && !key.trim().isEmpty()) {
            return key;
        }
        // 兜底：返回内置默认公钥（与 RsaKeyConfig 一致）
        return "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCPqwWQEQqXfPxa3GFUvNR+1rkMXmLSlXZqeBEYrXqjCxVp+cdKIDPOKjiapW06RSuGEBWQuKwNyG1IpDRLddifZJ0TZQEd3BNkaIqfUz4RE+qhnIb48dcJQwQdiwvTfrkLCky67aczhRt8kwcOvG0dK68QVI2xKowT4BMGkowfAwIDAQAB";
    }


    /**
     * 从 RsaKeyConfig 获取私钥（可能为 null）
     */
    private static String getPrivateKeyFromConfig() {
        if (applicationContext == null) {
            return null;
        }
        try {
            RsaKeyConfig config = applicationContext.getBean(RsaKeyConfig.class);
            return config.getPrivateKey();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从 RsaKeyConfig 获取公钥（可能为 null）
     */
    private static String getPublicKeyFromConfig() {
        if (applicationContext == null) {
            return null;
        }
        try {
            RsaKeyConfig config = applicationContext.getBean(RsaKeyConfig.class);
            return config.getPublicKey();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 私钥解密
     *
     * @param text 待解密的文本
     * @return 解密后的文本
     */
    public static String decryptByPrivateKey(String text) throws Exception
    {
        return decryptByPrivateKey(getPrivateKey(), text);
    }

    /**
     * 公钥解密
     *
     * @param text 待解密的信息
     * @return 解密后的文本
     */
    public static String decryptByPublicKey(String text) throws Exception
    {
        return decryptByPublicKey(getPublicKey(), text);
    }

    /**
     * 公钥解密
     *
     * @param publicKeyString 公钥
     * @param text 待解密的信息
     * @return 解密后的文本
     */
    public static String decryptByPublicKey(String publicKeyString, String text) throws Exception
    {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKeyString));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] result = cipher.doFinal(Base64.decodeBase64(text));
        return new String(result);
    }

    /**
     * 私钥加密
     *
     * @param privateKeyString 私钥
     * @param text 待加密的信息
     * @return 加密后的文本
     */
    public static String encryptByPrivateKey(String privateKeyString, String text) throws Exception
    {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKeyString));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] result = cipher.doFinal(text.getBytes());
        return Base64.encodeBase64String(result);
    }

    /**
     * 私钥解密
     *
     * @param privateKeyString 私钥
     * @param text 待解密的文本
     * @return 解密后的文本
     */
    public static String decryptByPrivateKey(String privateKeyString, String text) throws Exception
    {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec5 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKeyString));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec5);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] result = cipher.doFinal(Base64.decodeBase64(text));
        return new String(result);
    }

    /**
     * 公钥加密
     *
     * @param publicKeyString 公钥
     * @param text 待加密的文本
     * @return 加密后的文本
     */
    public static String encryptByPublicKey(String publicKeyString, String text) throws Exception
    {
        X509EncodedKeySpec x509EncodedKeySpec2 = new X509EncodedKeySpec(Base64.decodeBase64(publicKeyString));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec2);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] result = cipher.doFinal(text.getBytes());
        return Base64.encodeBase64String(result);
    }

    /**
     * 构建RSA密钥对（保留生成能力）
     *
     * @return 生成后的公私钥信息
     */
    public static RsaKeyPair generateKeyPair() throws NoSuchAlgorithmException
    {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        String publicKeyString = Base64.encodeBase64String(rsaPublicKey.getEncoded());
        String privateKeyString = Base64.encodeBase64String(rsaPrivateKey.getEncoded());
        return new RsaKeyPair(publicKeyString, privateKeyString);
    }

    /**
     * RSA密钥对对象
     */
    public static class RsaKeyPair
    {
        private final String publicKey;
        private final String privateKey;

        public RsaKeyPair(String publicKey, String privateKey)
        {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public String getPublicKey()
        {
            return publicKey;
        }

        public String getPrivateKey()
        {
            return privateKey;
        }
    }
}
