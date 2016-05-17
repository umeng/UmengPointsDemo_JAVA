package com.company.umeng;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by umeng on 5/13/16.
 */
public class MD5Util {
    public static String getMD5(String inStr) throws NoSuchAlgorithmException {
        MessageDigest md5 = null;
        try{
            md5 = MessageDigest.getInstance("MD5");
        }catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
//        char[] charArray = inStr.toCharArray();
//        byte[] byteArray = new byte[charArray.length];

//        for (int i = 0; i < charArray.length; i++)
//            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(inStr.getBytes());
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++){
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }


        return hexValue.toString();
    }



//    public static String getMD5_2(String input) {
//        try {
//            MessageDigest md = MessageDigest.getInstance("MD5");
//            byte[] messageDigest = md.digest(input.getBytes());
//            BigInteger number = new BigInteger(1, messageDigest);
//            String hashtext = number.toString(16);
//            // Now we need to zero pad it if you actually want the full 32 chars.
//            while (hashtext.length() < 32) {
//                hashtext = "0" + hashtext;
//            }
//            return hashtext;
//        }
//        catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
