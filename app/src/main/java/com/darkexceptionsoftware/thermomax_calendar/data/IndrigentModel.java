package com.darkexceptionsoftware.thermomax_calendar.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class IndrigentModel {

    @PrimaryKey(autoGenerate = true)
    public int uid;
    @ColumnInfo(name = "name_de")
    private String name_de;
    @ColumnInfo(name = "name_en")
    private String name_en;
    @ColumnInfo(name = "type")
    private int type;
    @ColumnInfo(name = "price")
    private int price;

    public IndrigentModel(String name_de, int type) {
        this.name_de = name_de;
        this.type = type;
    }

    public String getName_de() {
        return name_de;
    }

    public void setName_de(String name_de) {
        this.name_de = name_de;
    }

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
