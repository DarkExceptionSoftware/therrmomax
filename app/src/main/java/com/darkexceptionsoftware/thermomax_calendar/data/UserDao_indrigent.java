package com.darkexceptionsoftware.thermomax_calendar.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao_indrigent {
    @Query("SELECT * FROM IndrigentModel")
    List<IndrigentModel> getAll();

    @Query("SELECT * FROM IndrigentModel WHERE uid IN (:userIds)")
    List<IndrigentModel> loadAllByIds(int[] userIds);

    //@Query("SELECT * FROM DateModel WHERE name LIKE :first AND " +
     //       "last_name LIKE :last LIMIT 1")
    // DateModel findByName(String first, String last);

    @Insert
    void insertAll(IndrigentModel... users);

    @Delete
    void delete(IndrigentModel user);

    @Query("DELETE FROM IndrigentModel")
    void delete();

    @Query("DELETE FROM IndrigentModel WHERE name_de = :name")
    void deleteByName(String name);
}

