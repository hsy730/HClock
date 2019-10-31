package com.example.helloworld.utils;

import android.util.Log;

import com.example.helloworld.sqlite.WorkTimeRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TimeUtil {
    private String TAG = "TimeUtil";

    /**
     * 获取当前时间，时分
     * @return
     */
    public String getCurrentTimeStr() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(date);
    }

    /**
     * 查询年月，2019-08
     * @return
     */
    public String getCurYearMonth() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        return dateFormat.format(date);
    }

    /**
     * 查询年月日，2019-08-15
     * @return
     */
    public String getCurYearMonthDay() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    public boolean isToday(String month, String day) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Log.i(TAG, "isToday: 获取日期/"+dateFormat.format(date)+"日历日期/"+String.format("%s-%s",month, day));
        if (dateFormat.format(date).equals(String.format("%s-%s",month, day))){
            return true;
        }
        return false;
    }

    public String getCurrentDayInMonth() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        return dateFormat.format(date);
    }

    /**
    *@param t1 开始时间,时:分
    *@param t2 结束时间,时:分
    * @return 时间差,单位(分钟)
    */
    public int calcMinuteBetween(String t1,String t2){
        if(t2==null || t1==null || t1.compareTo(t2)>0){
            return -1;
        }
        String[] arrStrT1 = splitTimeStr(t1);
        String[] arrStrT2 = splitTimeStr(t2);
        int[] arrIntT1 = new int[]{Integer.parseInt(arrStrT1[0]), Integer.parseInt(arrStrT1[1])};
        int[] arrIntT2 = new int[]{Integer.parseInt(arrStrT2[0]), Integer.parseInt(arrStrT2[1])};
        int hour = arrIntT2[0] - arrIntT1[0];
        int minu = arrIntT2[1] - arrIntT1[1];
        // 减法进位
        if(minu < 0){
            minu += 60;
            hour -= 1;
        }
        return hour*60 + minu;
    }

    /**
     *@param t1 开始时间,时:分,xx:xx
     *@param t2 结束时间,时:分,xx:xx
     * @return 时间差 xx:xx
     */
    public String calcTimeBetween(String t1,String t2){
        if(t2==null || t1==null || t1.compareTo(t2)>0){
            return null;
        }
        String[] arrStrT1 = splitTimeStr(t1);
        String[] arrStrT2 = splitTimeStr(t2);
        int[] arrIntT1 = new int[]{Integer.parseInt(arrStrT1[0]), Integer.parseInt(arrStrT1[1])};
        int[] arrIntT2 = new int[]{Integer.parseInt(arrStrT2[0]), Integer.parseInt(arrStrT2[1])};
        int hour = arrIntT2[0] - arrIntT1[0];
        int minu = arrIntT2[1] - arrIntT1[1];
        // 减法进位
        if(minu < 0){
            minu += 60;
            hour -= 1;
        }
        String s1 = (hour<10)? ("0"+ hour) : String.valueOf(hour);
        String s2 =  (minu<10)? ("0"+ minu) : String.valueOf(minu);
        return String.format("%s:%s",s1, s2);
    }

    /**
     *@param t1 开始时间,时:分
     *@param t2 结束时间,时:分
     * @return 时间累加
     */
    public String sumTime(String t1,String t2){
        if(t2==null || t1==null ){
            return null;
        }
        String[] arrStrT1 = splitTimeStr(t1);
        String[] arrStrT2 = splitTimeStr(t2);
        int[] arrIntT1 = new int[]{Integer.parseInt(arrStrT1[0]), Integer.parseInt(arrStrT1[1])};
        int[] arrIntT2 = new int[]{Integer.parseInt(arrStrT2[0]), Integer.parseInt(arrStrT2[1])};
        int hour = arrIntT2[0] + arrIntT1[0];
        int minu = arrIntT2[1] + arrIntT1[1];
        // 加法进位
        if(minu > 59){
            minu -= 60;
            hour += 1;
        }
        return String.format("%s:%s",String.valueOf(hour), String.valueOf(minu));
    }

    public boolean isWeekEnd(String date){
        String dayInWeek = getDayOfWeekByDate(date);
        Log.i(TAG, "isWeekEnd: "+dayInWeek);
        // 周末算加班时间
        if("周六".equals(dayInWeek) || "周日".equals(dayInWeek)) {
           return true;
        }
        return false;
    }

    /**
     * 有效打卡
     * @param bg
     * @param ed
     * @return
     */
    public boolean isValidClockInWeekDay(String bg, String ed){
        // 迟到
        if (bg.compareTo("09:00") > 0) {
            return false;
        }
        // 一天上班9小时，540分钟
        if (calcMinuteBetween(bg, ed)< 540) {
            return false;
        }
        return true;
    }

    /**
     * 检查输入时间的合法性
     * @param str
     * @return
     */
    public boolean checkTimeFormat(String str){
        //长度
        if (str.length() != 5) {
            return false;
        }
        //字符格式
        for (int i = 0; i < 5; i++) {
            char c = str.charAt(i);
            if (i != 2) {
                if (c < 48 || c > 57) { return false; }
            } else {
                if (c < 48 || c > 57) {
                    // valid，skip it
                }else { return false; }
            }
        }
        //数字大小，时间格式
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

    /**
     * 格式化输入时间。输入要求不严格，中间不是数字即可
     * @param time
     * @return
     */
    public String formatClockTime(String time) {
        return String.format("%s:%s",time.substring(0,2),time.substring(3));
    }

    /**
     * 根据日期 找到对应日期的 星期
     * @return 星期一~星期日
     */
    public String getDayOfWeekByDate(String date) {
        String dayOfweek = "";
        try {
            SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
            Date myDate = myFormatter.parse(date);
            SimpleDateFormat formatter = new SimpleDateFormat("E");
            String str = formatter.format(myDate);
            dayOfweek = str;
        } catch (Exception e) {
            Log.e(TAG, "getDayOfWeekByDate: ", e);
            e.printStackTrace();
//            return null;
        }
        return dayOfweek;
    }

    /**
     *
     * @param records
     * @return 月加班时间总和(小时)
     */
    public double sumOverTimeInAMonth(ArrayList<WorkTimeRecord> records) {
        int totalOverTime = 0;
        for (WorkTimeRecord record:records) {
            String bg = record.getBeginTime();
            String ed = record.getEndTime();
            Log.i(TAG, "sumOverTimeInAMonth: "+record.toString());
//            System.out.println(record.toString());
            if (ed != null && bg != null ) {
                String date = String.format("%s-%s",record.getMonth(),record.getDay());
//                System.out.println("date:"+date);
                // 周末整天都算加班
                if (isWeekEnd(date)) {
                    totalOverTime += calcMinuteBetween(bg,ed);
                    Log.i(TAG, "sumOverTimeInAMonth: 周末加班"+ calcMinuteBetween(bg,ed));
//                    System.out.println("周末加班:"+calcMinuteBetween(bg,ed));
                } else {
                    // 周内对签到签退时间有要求
                    if (isValidClockInWeekDay(bg,ed)) {
                        // 6pm以后算加班
                        int overTime = calcMinuteBetween("18:00",ed);
                        totalOverTime += (overTime > 0) ? overTime : 0;
                        Log.i(TAG, "sumOverTimeInAMonth: 周内加班"+ calcMinuteBetween("18:00",ed));
//                        System.out.println("周内加班:"+ calcMinuteBetween("18:00",ed));
                    } else {
                        Log.w(TAG, "initTotalWorkTime: 考勤异常/"+ date );
//                        System.out.println("考勤异常");
                    }
                }
            }
        }
        Log.i(TAG, " ====================================");
        return (double)totalOverTime/60;
    }

    public String[] splitTimeStr(String s) {
        return new String[]{s.substring(0,2), s.substring(3)};
    }
}
