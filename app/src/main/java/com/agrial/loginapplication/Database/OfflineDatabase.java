package com.agrial.loginapplication.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.agrial.loginapplication.Model.OfflineModel;
import com.agrial.loginapplication.OfflineDao.OfflineDao;

@Database(entities = {OfflineModel.class}, version = 2)
public abstract class OfflineDatabase extends RoomDatabase
{
    public abstract OfflineDao offlineDao();
}