package com.example.helloworld;

import com.example.helloworld.utils.TimeUtil;

import org.junit.Test;

import java.text.SimpleDateFormat;
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
}