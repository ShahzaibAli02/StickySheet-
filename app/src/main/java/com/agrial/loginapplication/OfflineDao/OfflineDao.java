package com.agrial.loginapplication.OfflineDao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.agrial.loginapplication.Model.OfflineModel;

import java.util.List;

@Dao
public interface OfflineDao
{
    @Query("SELECT * FROM offlinemodel")
    List<OfflineModel> getAll();
    @Insert
    void insertAll(OfflineModel... users);

    @Delete
    void delete(OfflineModel user);
}