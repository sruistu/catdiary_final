package com.friends.catdiary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.friends.catdiary.db.Diary;
import com.friends.catdiary.db.DiaryDao;
import com.friends.catdiary.db.DiaryDatabase;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {

    private DiaryDao diaryDao;
    DiaryDatabase database;
    ListView listView;
    List<String> list;
    String tag;
    long day;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_day);

        initDataBase();
        initView();
        tagView();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.dayListView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                long day_tag = Long.parseLong(list.get(i));

                Intent cal = new Intent(SearchResultActivity.this, ReadDiaryActivity.class);
                cal.putExtra("checkId", day_tag);
                startActivity(cal);

            }
        });

    }


    private void initDataBase() {
        //DB 사용할 경우 모든 Class에 넣어줘야한다.
        database = Room.databaseBuilder(getApplicationContext(), DiaryDatabase.class, "cat_diary")
                .fallbackToDestructiveMigration()       //스키마 버전 변경가능
                .allowMainThreadQueries()               //Main Thread에서 DB에 I/O를 가능
                .build();
        diaryDao = database.userDao(); //인터페이스 객체할당
    }


    private void tagView() {

        list = new ArrayList<>();

        Intent intent = getIntent(); //전달할 데이터를 받을 Intent
        tag = intent.getStringExtra("tag");

        List<Diary> allFavoriteDiary = getAllFavoriteDayByDatabase();

        for (Diary diary : allFavoriteDiary) {
            checkFavoriteDay(diary);
            list.add(String.valueOf(day));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

    }

    private List<Diary> getAllFavoriteDayByDatabase() {
        return diaryDao.getTagDay(tag);
    }

    private void checkFavoriteDay(Diary diary) {
        day = diary.uid;
    }

}
