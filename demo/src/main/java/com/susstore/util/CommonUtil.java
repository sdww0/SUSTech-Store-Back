package com.susstore.util;

import java.util.regex.Pattern;

public class CommonUtil {


    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

}
