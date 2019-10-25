package com.example.helloworld.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME="mysql.db";
    private static final int VERSION=1;
    private static final String CREATE_TABLE_WORK_TIME="create table work_time(_id integer primary key autoincrement,"+
            "month text,day text,beginTime text,endTime text)";
    private static final String DROP_TABLE_WORK_TIME="DROP TABLE IF EXISTS work_time";
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, null, VERSION);
    }
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    //如果数据库表不存在，那么会调用该方法
    @Override
    public void onCreate(SQLiteDatabase db) {
        //SQLiteDatabase 用于操作数据库的工具类
//        System.out.println("create dog");
        Log.i("DatabaseHelper", "onCreate: create work_time");
        db.execSQL(CREATE_TABLE_WORK_TIME);
    }

    //升级更新数据库
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_WORK_TIME);
        db.execSQL(CREATE_TABLE_WORK_TIME);
    }
}

