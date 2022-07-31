package com.darkexceptionsoftware.thermomax_calendar.data;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Indrigent implements Serializable {
    private String name;
    private Float amount;
    private String amountof;
    private String sortof;




    public String getAmountof() {
        return amountof;
    }

    public void setAmountof(String amountof) {
        this.amountof = amountof;
    }

    public String getSortof() {
        return sortof;
    }

    public void setSortof(String sortof) {
        this.sortof = sortof;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getAmountString() {
        return String.valueOf(amount);
    }

    public boolean serialize(String _RecDir, String _filename){

        File myFile = new File(_RecDir + _filename + "/",  "recipe.ind");
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
