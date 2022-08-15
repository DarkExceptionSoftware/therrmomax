package com.darkexceptionsoftware.thermomax_calendar.data;

public class Zutat {

    private String name;
    private String menge;
    private String einheit;

    public Zutat(String menge, String einheit, String name) {
        this.menge = menge;
        this.einheit = einheit;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMenge() {
        return menge;
    }

    public void setMenge(String menge) {
        this.menge = menge;
    }

    public String getEinheit() {
        return einheit;
    }

    public void setEinheit(String einheit) {
        this.einheit = einheit;
    }
}
