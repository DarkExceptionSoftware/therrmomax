package com.darkexceptionsoftware.thermomax_calendar.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Indrigent implements Serializable , Parcelable {
    private String name;
    private Float amount;
    private String amountof;
    private int sortof;

    public Indrigent() {
    }

    public Indrigent(Float amount,String amountof, String name,int sortof) {
        this.amount = amount;
        this.amountof = amountof;
        this.name = name;
        this.sortof = sortof;
    }

    public Indrigent(Parcel in) {
        name = in.readString();
        if (in.readByte() == 0) {
            amount = null;
        } else {
            amount = in.readFloat();
        }
        amountof = in.readString();
        sortof = in.readInt();
    }

    public static final Creator<Indrigent> CREATOR = new Creator<Indrigent>() {
        @Override
        public Indrigent createFromParcel(Parcel in) {
            return new Indrigent(in);
        }

        @Override
        public Indrigent[] newArray(int size) {
            return new Indrigent[size];
        }
    };

    public String getAmountof() {
        return amountof;
    }

    public void setAmountof(String amountof) {
        this.amountof = amountof;
    }

    public int getSortof() {
        return sortof;
    }

    public void setSortof(int sortof) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(name);
        if (amount == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeFloat(amount);
        }
        parcel.writeString(amountof);
        parcel.writeInt(sortof);
    }
}
