package com.example.helloworld.utils;

public class StringUtil {
    public static boolean isEmpty(String s) {
        if (s != null && !"".equals(s)) {
            return false;
        }
        return true;
    }
    // 08,数字前面带0
    public static int toInt(String s) {
        char[] c = s.toCharArray();
        for(int i=0;i<c.length;i++) {
            if (c[i]!='0') {
                try {
                    return Integer.parseInt(s.substring(i));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }
}
