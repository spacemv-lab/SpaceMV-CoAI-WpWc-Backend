package com.ruoyi.common.core.utils.sign;

import org.apache.commons.codec.binary.Base64;
import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA加密解密
 *
 * @author ruoyi
 **/
public class RsaUtils
{
    // Rsa 私钥
    public static String privateKey = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAKH+tcTJJygbQmbXkE5hahX/kndH9" +
            "q6uvm6SotBUrBb0CUaCYkrUg6m5sMPMrWskbHr2O0HsGYNn7XtmQKleeKyGoiUa/cS/tkKieNsMjZnjIZTq20hKeC/c2cFghXftEUVKdP5" +
            "wNhL5BTDU7uBVV9U61OIJVcfj/ZXIUFDw8GcTAgMBAAECgYAEGWV8Sa+dpkjSE1ZMoijzsDOZShxlDuoteofoeufLyIsn5WbMGzEaURBkDtU8dt" +
            "3ulJ8q9SUgfApDw5+pyYTSOy0BsfqBtT9G1qp+vmx3pFjao34Rx61NPbUbQDY/9LRxUQL+1xHEMru+/yU/5gJZmfdxN80sUHUd9dkFR1WK7QJBAP" +
            "/uVJBRbU44RbTM53ZvtkTX2BynXzZpm3xLme+wRS59QfF/gNLKyLUa7xOJ7Obc1oJAcSPvhM59pJ/tZrCwpHcCQQCiCeTwSdJoWuR8+jrKHEflF+I1cT9gh" +
            "Qvkcd/+AGil+OXcfsvIaYA1JloZQT+7FLbRlvWmkhR3tMVhxMAzAEVFAkEAyWdO2PubXzDdeji99fBXqbmKcpIsVW+qUphUHdHDv6AG4vuJ71hxtkPp3KBv9AXa" +
            "MUpxPuxgwPcTNF/orid0ZQJBAJoeixA5RtVWzkhIwK4HpCI0S0XFhyBIq30HCqNOxDpIuGi2eSEPp4/mAIBQ3UsVcqV6zf82ph0NZUeOmbhoo3kCQQC//ZVASAo" +
            "QYelnZycjguwDUQwkCfTDJiKJ+heGPZ9Bl8UjrfiIIhHRkhUMFPl1rwoYI7wjjEdD5afOK7G7gEny";

    public static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCh/rXEyScoG0Jm15BOYWoV/5J3R/aurr5ukqLQVKwW9AlGgmJK1IOpubDDzK1rJGx69jtB7BmD" +
            "Z+17ZkCpXnishqIlGv3Ev7ZConjbDI2Z4yGU6ttISngv3NnBYIV37RFFSnT+cDYS+QUw1O7gVVfVOtTiCVXH4/2VyFBQ8PBnEwIDAQAB";

    /**
     * 私钥解密
     *
     * @param text 待解密的文本
     * @return 解密后的文本
     */
    public static String decryptByPrivateKey(String text) throws Exception
    {
        return decryptByPrivateKey(privateKey, text);
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
     * 构建RSA密钥对
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