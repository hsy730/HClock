package com.example.helloworld.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DatabaseAdaper {
    String[] colums={MetaData.WTRTable._ID, MetaData.WTRTable.MONTH, MetaData.WTRTable.DAY,MetaData.WTRTable.BEGIN_TIME, MetaData.WTRTable.END_TIME};
    private DatabaseHelper databaseHelper;
    public DatabaseAdaper(Context context){
        databaseHelper=new DatabaseHelper(context);
    }
    //添加操作
    public void add(WorkTimeRecord record){
        SQLiteDatabase db=databaseHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(MetaData.WTRTable.MONTH,record.getMonth());
        values.put(MetaData.WTRTable.DAY,record.getDay());
        values.put(MetaData.WTRTable.BEGIN_TIME,record.getBeginTime());
        values.put(MetaData.WTRTable.END_TIME,record.getEndTime());
        //参数（表名，可以为null的列名，更新字段的集合ContentValues）
        //合法：insert into dog(name,age) values('xx',2)
        //不合法：insert into dog() values()
        db.insert(MetaData.WTRTable.TABLE_NAME,null,values);
        db.close();
    }
    //删除操作
    public void delete(int id){
        SQLiteDatabase db=databaseHelper.getWritableDatabase();
        String whereClause= MetaData.WTRTable._ID+"=?";
        String[] whereArgs= {String.valueOf(id)};
        //表名，删除条件，条件的值
        db.delete(MetaData.WTRTable.TABLE_NAME,whereClause,whereArgs);
        db.close();
    }
    //凭id更新
//    public void update(WorkTimeRecord record){
//        SQLiteDatabase db=databaseHelper.getWritableDatabase();
//        ContentValues values=new ContentValues();
//        values.put(MetaData.WTRTable.MONTH,record.getMonth());
//        values.put(MetaData.WTRTable.DAY,record.getDay());
//        values.put(MetaData.WTRTable.BEGIN_TIME,record.getBeginTime());
//        values.put(MetaData.WTRTable.END_TIME,record.getEndTime());
//        String whereClause= MetaData.WTRTable._ID +"=?";
//        String[] whereArgs= {String.valueOf(record.getId())};
//        //表名，更新字段的集合ContentValues，条件，条件的值
//        db.update(MetaData.WTRTable.TABLE_NAME,values,whereClause,whereArgs);
//    }

    // 凭month，day更新
    public void update(WorkTimeRecord record){
        SQLiteDatabase db=databaseHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(MetaData.WTRTable.MONTH,record.getMonth());
        values.put(MetaData.WTRTable.DAY,record.getDay());
        values.put(MetaData.WTRTable.BEGIN_TIME,record.getBeginTime());
        values.put(MetaData.WTRTable.END_TIME,record.getEndTime());
        String whereClause= MetaData.WTRTable.MONTH + "=? and " + MetaData.WTRTable.DAY + "=?";
        String[] whereArgs= {record.getMonth(),record.getDay()};
        //表名，更新字段的集合ContentValues，条件，条件的值
        db.update(MetaData.WTRTable.TABLE_NAME,values,whereClause,whereArgs);
    }

    //凭id查询
    public WorkTimeRecord findById(int id){
        SQLiteDatabase db=databaseHelper.getReadableDatabase();
        //是否去除重复记录，参数（表名，要查询的列，查询条件，查询条件的值，分组条件，分组条件的值，排序，分页）
        Cursor c=db.query(true, MetaData.WTRTable.TABLE_NAME,colums, MetaData.WTRTable._ID+"=?",new String[]{String.valueOf(id)},null,null,null,null);
        WorkTimeRecord record=null;
        if (c.moveToNext()){
            record=new WorkTimeRecord();
            record.setId(c.getInt(c.getColumnIndexOrThrow(MetaData.WTRTable._ID)));
            record.setMonth(c.getString(c.getColumnIndexOrThrow(MetaData.WTRTable.MONTH)));
            record.setDay(c.getString(c.getColumnIndexOrThrow(MetaData.WTRTable.DAY)));
            record.setBeginTime(c.getString(c.getColumnIndexOrThrow(MetaData.WTRTable.BEGIN_TIME)));
            record.setEndTime(c.getString(c.getColumnIndexOrThrow(MetaData.WTRTable.END_TIME)));
        }
        c.close();
        db.close();
        return record;
    }
    //查询所有
    public ArrayList<WorkTimeRecord> findAll(){
        SQLiteDatabase db=databaseHelper.getReadableDatabase();
        //是否去除重复记录，参数（表名，要查询的列，查询条件，查询条件的值，分组条件，分组条件的值，排序，分页）
        Cursor c=db.query(true, MetaData.WTRTable.TABLE_NAME,colums,null,null,null,null,null,null);
        ArrayList<WorkTimeRecord> records = new ArrayList<>();
        WorkTimeRecord record=null;
        while (c.moveToNext()){
            record=new WorkTimeRecord();
            record.setId(c.getInt(c.getColumnIndexOrThrow(MetaData.WTRTable._ID)));
            record.setMonth(c.getString(c.getColumnIndexOrThrow(MetaData.WTRTable.MONTH)));
            record.setDay(c.getString(c.getColumnIndexOrThrow(MetaData.WTRTable.DAY)));
            record.setBeginTime(c.getString(c.getColumnIndexOrThrow(MetaData.WTRTable.BEGIN_TIME)));
            record.setEndTime(c.getString(c.getColumnIndexOrThrow(MetaData.WTRTable.END_TIME)));
            records.add(record);
        }
        c.close();
        db.close();
        return records;
    }

    /**
     * 获取日打卡记录
     * @param month
     * @param day
     * @return a record
     */
    public WorkTimeRecord getRecordAtDayOfMonth(String month,String day){
        SQLiteDatabase db=databaseHelper.getReadableDatabase();
        //是否去除重复记录，参数（表名，要查询的列，查询条件，查询条件的值，分组条件，分组条件的值，排序，分页）
        Cursor c=db.query(true, MetaData.WTRTable.TABLE_NAME,colums, MetaData.WTRTable.MONTH
                +"=? and "+MetaData.WTRTable.DAY + "=?",new String[]{month,day},null,null,null,null);
        WorkTimeRecord record=null;
        if (c.moveToNext()){
            record = new WorkTimeRecord();
            record.setId(c.getInt(c.getColumnIndexOrThrow(MetaData.WTRTable._ID)));
            record.setMonth(c.getString(c.getColumnIndexOrThrow(MetaData.WTRTable.MONTH)));
            record.setDay(c.getString(c.getColumnIndexOrThrow(MetaData.WTRTable.DAY)));
            record.setBeginTime(c.getString(c.getColumnIndexOrThrow(MetaData.WTRTable.BEGIN_TIME)));
            record.setEndTime(c.getString(c.getColumnIndexOrThrow(MetaData.WTRTable.END_TIME)));
        }
        c.close();
        db.close();
        return record;
    }

    /**
     * 获取月打卡记录
     * @param month
     * @return records
     */
    public ArrayList<WorkTimeRecord> getRecordsInMonth(String month){
        SQLiteDatabase db=databaseHelper.getReadableDatabase();
        //是否去除重复记录，参数（表名，要查询的列，查询条件，查询条件的值，分组条件，分组条件的值，排序，分页）
        Cursor c=db.query(true, MetaData.WTRTable.TABLE_NAME, colums, MetaData.WTRTable.MONTH
                + "=?",new String[]{month},null,null,null,null);
        ArrayList<WorkTimeRecord> records = new ArrayList<>();
        WorkTimeRecord record=null;
        while (c.moveToNext()){
            record = new WorkTimeRecord();
            record.setId(c.getInt(c.getColumnIndexOrThrow(MetaData.WTRTable._ID)));
            record.setMonth(c.getString(c.getColumnIndexOrThrow(MetaData.WTRTable.MONTH)));
            record.setDay(c.getString(c.getColumnIndexOrThrow(MetaData.WTRTable.DAY)));
            record.setBeginTime(c.getString(c.getColumnIndexOrThrow(MetaData.WTRTable.BEGIN_TIME)));
            record.setEndTime(c.getString(c.getColumnIndexOrThrow(MetaData.WTRTable.END_TIME)));
            records.add(record);
        }
        c.close();
        db.close();
        return records;
    }
}

