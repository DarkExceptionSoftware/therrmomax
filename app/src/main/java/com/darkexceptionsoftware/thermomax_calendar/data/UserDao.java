package com.darkexceptionsoftware.thermomax_calendar.data;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RoomDatabase;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM DateModel")
    List<DateModel> getAll();

    @Query("SELECT * FROM DateModel WHERE uid IN (:userIds)")
    List<DateModel> loadAllByIds(int[] userIds);

    //@Query("SELECT * FROM DateModel WHERE name LIKE :first AND " +
     //       "last_name LIKE :last LIMIT 1")
    // DateModel findByName(String first, String last);

    @Insert
    void insertAll(DateModel... users);

    @Delete
    void delete(DateModel user);

    @Query("DELETE FROM DateModel")
    void delete();
}

