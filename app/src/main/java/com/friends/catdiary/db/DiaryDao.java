package com.friends.catdiary.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;


@Dao
public interface DiaryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDiary(Diary user);

    @Query("SELECT * FROM Diary WHERE uid=:date")
    Diary getSpecificDiary(Long date);

    @Query("SELECT diaryTag FROM Diary")
    List<String> getAllTag();

    @Query("DELETE FROM Diary WHERE uid=:date")
    void deleteDate(Long date);

    @Query("DELETE FROM Diary")
    void deleteAll();

    @Query("SELECT * FROM Diary WHERE diaryTag=:tagname ")
    List<Diary> getTagDay(String tagname);

    @Query("SELECT * FROM Diary WHERE Star= 1")
    List<Diary> getFavoriteAllDays();

    @Query("SELECT uid FROM Diary WHERE Star= 1")
    List<Long> getFavoriteDate();

    @Query("SELECT * FROM Diary")
    List<Diary> getAllWriteDays();
}
