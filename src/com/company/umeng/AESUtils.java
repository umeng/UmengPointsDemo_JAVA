package com.company.umeng;

import com.sun.org.apache.xml.internal.security.utils.*;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.Base64;

/**
 * Created by umeng on 5/13/16.
 */
public class AESUtils {
    private final static Charset CHARSET    = Charset.forName("utf-8");
    private final static int     BLOCK_SIZE = 32;

    public static String getEncryptedMap(String plaintext,String appSecret) {
        if(null==plaintext){
            throw new RuntimeException("data is null");
        }
        // 加密
        String encrypt = encrypt(plaintext,appSecret);
        return  encrypt;
    }

    /*
     * 对明文加密.
     * @param text 需要加密的明文
     * @return 加密后base64编码的字符串
     */
    private static String encrypt(String plaintext,String appSecret){

        try {
            byte[] plainTextBytes = plaintext.getBytes(CHARSET);
            //int length = plaintext.length();
            //byte [] fourByteArray  = new byte[]{(byte)(length&0xff)};
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            //byteArrayOutputStream.write(fourByteArray);
            byteArrayOutputStream.write(plainTextBytes);
            byteArrayOutputStream.write(getPaddingBytes(byteArrayOutputStream.size()));
            byte[] unencrypted = byteArrayOutputStream.toByteArray();
            //unencrypted = ByteBuffer.wrap(unencrypted).order(ByteOrder.BIG_ENDIAN).array();
            //Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(appSecret.getBytes(), "AES");
            IvParameterSpec iv = new IvParameterSpec(appSecret.getBytes(), 0, 16);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            byte[] encrypted = cipher.doFinal(unencrypted);
            String result = com.company.umeng.Base64.encodeToString(encrypted, com.company.umeng.Base64.URL_SAFE);
            //String result = Base64.getEncoder().encode(encrypted).toString();
            return result;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static byte[] getPaddingBytes(int count) {
        int amountToPad = BLOCK_SIZE - (count % BLOCK_SIZE);
        if (amountToPad == 0) {
            amountToPad = BLOCK_SIZE;
        }
        char padChr = chr(amountToPad);
        String tmp = new String();
        for (int index = 0; index < amountToPad; index++) {
            tmp += padChr;
        }
        return tmp.getBytes(CHARSET);
    }

    private static char chr(int a) {
        byte target = (byte) (a & 0xFF);
        return (char) target;
    }

}
