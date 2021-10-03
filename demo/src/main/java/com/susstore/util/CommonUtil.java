package com.susstore.util;

import java.util.regex.Pattern;

public class CommonUtil {

    private static Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");;

    public static boolean isInteger(String str) {
        return pattern.matcher(str).matches();
    }

}
