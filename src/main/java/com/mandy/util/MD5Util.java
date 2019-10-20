package com.mandy.util;

import org.springframework.util.DigestUtils;

/**
 * Created by MandyOu on 2019/10/19
 */
public class MD5Util {
    public static String md5(String src){
        return DigestUtils.md5DigestAsHex(src.getBytes());
    }

    private static final String salt = "1a2b3c4d";

    public static String inputPassToFormPass(String inputPass){
        String str = ""+salt.charAt(0)+salt.charAt(1)+inputPass + salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }

    public static String formPassToDBPass(String formPass,String salt){
        String str = ""+salt.charAt(0)+salt.charAt(1)+formPass + salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDBPass(String input,String salt){
        String formPass = inputPassToFormPass(input);
        String dbPass = formPassToDBPass(formPass,salt);
        return dbPass;
    }

    public static void main(String[] args) {
        System.out.println(inputPassToFormPass("123456"));//12123456c3
//        System.out.println(formPassToDBPass(inputPassToFormPass("123456"),"111111122"));
//        System.out.println(inputPassToDBPass("123456","1a2b3c4d"));
    }
}
