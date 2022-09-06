package com.darkexceptionsoftware.thermomax_calendar.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {IndrigentModel.class}, version = 1)
public abstract class AppDatabase_indrigent extends RoomDatabase {
    public abstract UserDao_indrigent userDao();
}
