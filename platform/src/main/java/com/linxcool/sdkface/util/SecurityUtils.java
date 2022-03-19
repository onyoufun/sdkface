package com.linxcool.sdkface.util;

import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SecurityUtils {
    /**
     * md5加密
     * @param input 16位或32位
     * @return
     */
    static String md5(String input) {
        try {
            String algorithm = System.getProperty("MD5.algorithm", "MD5");
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] bs = md.digest(input.getBytes("utf-8"));
            return bytesToHexString(bs);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 字节数组转16进制字符
     * @param src
     * @return
     */
    private static String bytesToHexString(byte[] src) {
        StringBuilder sb = new StringBuilder();
        if (src == null || src.length <= 0) return null;
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) sb.append(0);
            sb.append(hv);
        }
        return sb.toString();
    }

    /**
     * 先sha-256,然后对byte数组进行转16位字符串
     * @param str
     * @return
     */
    static String getSHA256(byte[] str) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str);
            return bytesToHexString(messageDigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 先 HmacSHA256 ,然后对byte数组进行转16位字符串
     * @param message
     * @param secret
     * @return
     */
    static String sha256_HMAC(byte[] message, String secret) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            return bytesToHexString(sha256_HMAC.doFinal(message));
        } catch (Exception var5) {
            var5.printStackTrace();
            System.out.println("Error HmacSHA256 ===========" + var5.getMessage());
            return null;
        }
    }
}
