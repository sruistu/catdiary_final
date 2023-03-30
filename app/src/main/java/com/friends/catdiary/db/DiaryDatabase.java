package com.friends.catdiary.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Diary.class}, version = 2)
public abstract class DiaryDatabase extends RoomDatabase {
    public abstract DiaryDao userDao();
}