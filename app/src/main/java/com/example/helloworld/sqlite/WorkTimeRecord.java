package com.example.helloworld.sqlite;

import lombok.Data;

@Data
public class WorkTimeRecord {
    private int id;
    private String month;
    private String day;
    private String beginTime;
    private String endTime;
    public WorkTimeRecord(){}
    // 1 , 201908 ,  5 , 08:43 , 16:54
    public WorkTimeRecord(int id, String month, String day, String beginTime, String endTime) {
        this.id = id;
        this.month = month;
        this.day = day;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    public WorkTimeRecord(String month,String day, String beginTime, String endTime) {
        this.month = month;
        this.day = day;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "WorkTimeRecord{" +
                "id=" + id + "\'" +
                ",month=" + month + "\'" +
                ",day='" + day + "\'" +
                ",beginTime='" + beginTime + "\'" +
                ",endTime=" + endTime + "\'" +
                "}";
    }
}

