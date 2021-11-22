package com.susstore.util;

import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
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

    public static String getIpAddress(HttpServletRequest request) {
        String ip = null;

        //X-Forwarded-For：Squid 服务代理
        String ipAddresses = request.getHeader("X-Forwarded-For");
        System.out.println("====ipAddresses:"+ipAddresses);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            //打印所有头信息
            String s = headerNames.nextElement();
            String header = request.getHeader(s);
            System.out.println(s+"::::"+header);
        }
        System.out.println("headerNames:"+ JSON.toJSONString(headerNames));
        System.out.println("RemoteHost:"+request.getRemoteHost());
        System.out.println("RemoteAddr:"+request.getRemoteAddr());

        String unknown = "unknown";
        if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
            //Proxy-Client-IP：apache 服务代理
            ipAddresses = request.getHeader("Proxy-Client-IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
            //WL-Proxy-Client-IP：weblogic 服务代理
            ipAddresses = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
            //HTTP_CLIENT_IP：有些代理服务器
            ipAddresses = request.getHeader("HTTP_CLIENT_IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
            //X-Real-IP：nginx服务代理
            ipAddresses = request.getHeader("X-Real-IP");
        }

        //有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
        if (ipAddresses != null && ipAddresses.length() != 0) {
            ip = ipAddresses.split(",")[0];
        }

        //还是不能获取到，最后再通过request.getRemoteAddr();获取
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }



}
