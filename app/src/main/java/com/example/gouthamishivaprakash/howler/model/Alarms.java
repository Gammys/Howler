package com.example.gouthamishivaprakash.howler.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Alarms extends RealmObject {
    @PrimaryKey
    private int id;
    private String alarmTime;
    private int requestCode;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public int getRequestCode() {
        return requestCode;
    }
}
