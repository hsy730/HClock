package com.example.helloworld;

import android.util.Log;

import com.example.helloworld.sqlite.WorkTimeRecord;
import com.example.helloworld.utils.StringUtil;
import com.example.helloworld.utils.TimeUtil;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private String TAG = "test";
    TimeUtil timeUtil = new TimeUtil();
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void getTime() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        System.out.println(dateFormat.format(date));
        System.out.println(getDayOfMonth());

    }
    public int getDayOfMonth(){
        Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
        int day=aCalendar.getActualMaximum(Calendar.DATE);
        return day;
    }
    @Test
    public void test() {
        String text = "08:7x";
        TimeUtil timeUtil = new TimeUtil();
        assertTrue(timeUtil.checkTimeFormat("12:24"));
        assertTrue(timeUtil.checkTimeFormat("23:59"));
        assertFalse(timeUtil.checkTimeFormat(text));
        assertFalse(timeUtil.checkTimeFormat("23:60"));
        assertFalse(timeUtil.checkTimeFormat("24:00"));
        assertTrue(timeUtil.checkTimeFormat("00:00"));
        assertFalse(timeUtil.checkTimeFormat("24:000"));
        assertFalse(timeUtil.checkTimeFormat("24600"));
    }
    @Test
    public void test1(){
        TimeUtil timeUtil = new TimeUtil();
        System.out.println(timeUtil.getCurrentDayInMonth());
        System.out.println(timeUtil.getCurYearMonth());
        System.out.println(timeUtil.getCurrentTimeStr());
    }
    @Test
    public void testGetDayOfWeek() {
        String x = timeUtil.getDayOfWeekByDate("2019-10-27");
//        Log.i(TAG, "testGetDayOfWeek: "+x);
        System.out.println(x);
    }
    @Test
    public void testComparStr() {
        assertTrue("08:55".compareTo("09:48")<0);
        assertTrue("08:44".compareTo("09:32")<0);
        assertTrue("08:55".compareTo("07:48")>0);


    }
    @Test
    public void testCalcTimeBetween() {
        TimeUtil timeUtil = new TimeUtil();
        System.out.println(   timeUtil.calcMinuteBetween("08:32","20:23"));
        System.out.println(   timeUtil.sumTime("1:32","2:23"));

    }
    @Test
    public void testToInt() {
        System.out.println(StringUtil.toInt("001045"));
        System.out.println(Integer.parseInt("08"));
    }
    @Test
    public void testSplitTime() {
        System.out.println(Arrays.toString(timeUtil.splitTimeStr("08.22")));
//        System.out.println(Integer.parseInt("08"));
    }
    @Test
    public void testIsWeekend() {
        System.out.println(timeUtil.isWeekEnd("2019-10-27"));
    }

    @Test
    public void testSumOverTime() {
        ArrayList<WorkTimeRecord> records = new ArrayList<>();

        records.add(new WorkTimeRecord("2019-10","27","10.32","22.14"));
        records.add(new WorkTimeRecord("2019-10","29","08:22","18:33"));

        double d = timeUtil.sumOverTimeInAMonth(records);
        System.out.println(d);
    }

    @Test
    public void testCalcMinuteBetween() {
        System.out.println(timeUtil.calcMinuteBetween("18.00","18.12"));
    }
    @Test
    public void testFormatTime() {
        System.out.println(timeUtil.formatClockTime("08.22"));
    }
    @Test
    public void testIsToday() {
        System.out.println(timeUtil.isToday("2019-11","01"));
    }
}