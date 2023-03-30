package com.friends.catdiary.db;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Diary {
    @PrimaryKey
    public long uid;

    @ColumnInfo(name = "diaryText")
    public String diaryText;

    @ColumnInfo(name = "diaryTag")
    public String diaryTag;

    @ColumnInfo(name = "Star")
    public boolean star;

    @ColumnInfo(name = "lockEntity")
    public boolean lockEntity;

    @ColumnInfo(name = "image")
    public String image;

}
