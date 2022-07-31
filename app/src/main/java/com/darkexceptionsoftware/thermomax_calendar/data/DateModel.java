package com.darkexceptionsoftware.thermomax_calendar.data;

import android.graphics.Color;

import java.util.Calendar;
import java.util.Date;

public class DateModel {
    private String modelID;
    private Date date;
    private String user;
    private int status;
    private int backcolor;

    public int getBackcolor() {
        return backcolor;
    }

    public void setBackcolor(int backcolor) {
        this.backcolor = backcolor;
    }

    public String getModel() {
        return modelID;
    }

    public void setModel(String modelID) {
        this.modelID = modelID;
    }

    public DateModel(String modelID, Date date, String user, int status, int backcolor) {
        this.modelID = modelID;
        this.date = date;
        this.user = user;
        this.status = status;
        this.backcolor = backcolor;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
