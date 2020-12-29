package com.orvillex.utils;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * 加解密工具类
 * @author y-z-f
 * @version 0.1
 */
public class EncryptUtils {
    
    private static final String STR_PARAM = "orvillex";
    private static Cipher cipher;
    private static final IvParameterSpec IV = new IvParameterSpec(STR_PARAM.getBytes(StandardCharsets.UTF_8));

    /**
     * 获取私钥
     */
    private static DESKeySpec getDesKeySpec(String source) throws Exception {
        if (source == null || source.isEmpty()) {
            return null;
        }
        cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        String strKey = "orvillexkey";
        return new DESKeySpec(strKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 加密
     */
    public static String desEncrypt(String source) throws Exception {
        DESKeySpec desKeySpec = getDesKeySpec(source);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IV);
        return byte2hex(cipher.doFinal(source.getBytes(StandardCharsets.UTF_8))).toUpperCase();
    }

    /**
     * 解密
     */
    public static String desDecrypt(String source) throws Exception {
        byte[] src = hex2byte(source.getBytes(StandardCharsets.UTF_8));
        DESKeySpec desKeySpec = getDesKeySpec(source);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IV);
        byte[] retByte = cipher.doFinal(src);
        return new String(retByte);
    }

    /**
     * 字节转16进制
     */
    private static String byte2hex(byte[] content) {
        String stmp;
        StringBuilder out = new StringBuilder(content.length * 2);
        for (byte b : content) {
            stmp = Integer.toHexString(b & 0xFF);
            if (stmp.length() == 1) {
                out.append("0").append(stmp);
            } else {
                out.append(stmp);
            }
        }
        return out.toString();
    }

    /**
     * 16进制转字节
     */
    private static byte[] hex2byte(byte[] content) {
        int size = 2;
        if ((content.length % size) != 0) {
            throw new IllegalArgumentException("长度不符");
        }
        byte[] bytes = new byte[content.length / 2];
        for (int n = 0; n < content.length; n += size) {
            String item = new String(content, n, 2);
            bytes[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return bytes;
    }
}
