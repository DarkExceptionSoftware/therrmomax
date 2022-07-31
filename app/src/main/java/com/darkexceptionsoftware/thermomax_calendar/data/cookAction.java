package com.darkexceptionsoftware.thermomax_calendar.data;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class cookAction implements Serializable {
    private String name;
    private int time;
    private String desc;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean serialize(String _RecDir, String _filename){

        File myFile = new File(_RecDir + _filename + "/",  "cooking.coa");
        if (!myFile.getParentFile().exists())
            myFile.getParentFile().mkdir();

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(myFile);
            ObjectOutputStream oout = null;
            oout = new ObjectOutputStream(new BufferedOutputStream(out));
            oout.writeObject(this);
            oout.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
