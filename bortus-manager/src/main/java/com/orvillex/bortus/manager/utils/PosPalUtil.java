package com.orvillex.bortus.manager.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PosPalUtil {
    
    public static String encryptToMd5String(String content,String appKey) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        return encryptToMd5String(StringUtils.trim (appKey)+StringUtils.trim(content));
    }
    
    public static String encryptToMd5String(String content) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String md5String = null;
        MessageDigest md = MessageDigest.getInstance("md5");
        md.update(content.getBytes("UTF-8"));
        md5String = parseByte2HexString(md.digest());
        return md5String;
    }
    
    public static String parseByte2HexString(byte buf[]) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            stringBuffer.append(hex.toUpperCase());
        }
        return stringBuffer.toString();
    }
}
