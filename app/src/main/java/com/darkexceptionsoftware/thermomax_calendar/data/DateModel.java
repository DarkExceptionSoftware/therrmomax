package com.darkexceptionsoftware.thermomax_calendar.data;

import android.graphics.Color;
import android.icu.text.SimpleDateFormat;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;
import java.util.Date;

@Entity
public class DateModel {
    private final static SimpleDateFormat format_ymd = new SimpleDateFormat("yyyy-MM-dd");
    private final static SimpleDateFormat format_day = new SimpleDateFormat("EE");
    @PrimaryKey(autoGenerate = true)
    public int uid;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "creation_date")
    private Long modelID;
    @ColumnInfo(name = "date")
    private Long date;
    @ColumnInfo(name = "user")
    private String user;
    @ColumnInfo(name = "status")
    private int status;
    @ColumnInfo(name = "color")
    private int backcolor;

    public DateModel(Long modelID, Long date, String user, int status, int backcolor) {
        this.modelID = modelID;
        this.date = date;
        this.user = user;
        this.status = status;
        this.backcolor = backcolor;
    }

    public Long getModelID() {
        return modelID;
    }

    public void setModelID(Long modelID) {
        this.modelID = modelID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBackcolor() {
        return backcolor;
    }

    public void setBackcolor(int backcolor) {
        this.backcolor = backcolor;
    }

    public Long getModel() {
        return modelID;
    }

    public void setModel(Long modelID) {
        this.modelID = modelID;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getStringDate(){

        String datestring = format_ymd.format(new Date(date));
        return datestring;
    }

    public String getDay(){

        String datestring = format_day.format(new Date(date)).replace(".","");
        return datestring;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
