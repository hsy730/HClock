package com.example.helloworld.sqlite;

import android.provider.BaseColumns;

import lombok.Data;

@Data
public class MetaData {
    private MetaData(){}
    //打卡日志表的定义
    public static abstract class WTRTable implements BaseColumns {
        public static final String TABLE_NAME="work_time";
        public static final String MONTH="month";
        public static final String DAY="day";
        public static final String BEGIN_TIME="beginTime";
        public static final String END_TIME="endTime";
    }
}

