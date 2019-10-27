package com.example.helloworld.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
//    private String daytimeFormat = "HH:mm";

    public String getCurrentTimeStr() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(date);
    }

    public String getCurYearMonth() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        return dateFormat.format(date);
    }

    public String getCurYearMonthDay() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    public String getCurrentDayInMonth() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        return dateFormat.format(date);
    }

    public boolean checkTimeFormat(String str){
        //长度
        if (str.length() != 5) {
            return false;
        }
        //字符格式
        for (int i = 0; i < 5; i++) {
            char c = str.charAt(i);
            if (i != 2) {
                if (c < 48 || c > 57) {
                    return false;
                }
            } else {
                if (c < 48 || c > 57) {
                    // valid
                }else {
                    return false;
                }
            }
        }
        //数字大小，时间格式
        char symbol = str.charAt(2);
        String partHour = str.substring(0,2);
        String partMinute = str.substring(3);
        return checkFormatOf(partHour,partMinute);
    }
    private boolean checkFormatOf(String hour,String minute) {
        if (hour.compareTo("00") >= 0 && hour.compareTo("24") < 0
                && minute.compareTo("0") >= 0 && minute.compareTo("60") < 0){
            return true;
        }
        return false;
    }
}
