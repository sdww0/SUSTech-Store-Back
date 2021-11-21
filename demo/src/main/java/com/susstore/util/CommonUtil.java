package com.susstore.util;

import java.util.Random;
import java.util.regex.Pattern;

public class CommonUtil {

    private static final Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
    private static final String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random random=new Random();

    public static boolean isInteger(String str) {
        return pattern.matcher(str).matches();
    }

    //length用户要求产生字符串的长度
    public static String getRandomString(int length){

        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
    //[left,right)
    public static Integer getRandomInteger(Integer left,Integer right){
        return random.nextInt((right-left))+left;
    }


}
