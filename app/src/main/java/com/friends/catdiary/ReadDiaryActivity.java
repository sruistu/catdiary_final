package com.friends.catdiary;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.friends.catdiary.calendar.CalendarActivity;
import com.friends.catdiary.db.Diary;
import com.friends.catdiary.db.DiaryDao;
import com.friends.catdiary.db.DiaryDatabase;
import com.friends.catdiary.R;
import com.friends.catdiary.setting.SettingActivity;

import java.io.IOException;

public class ReadDiaryActivity extends AppCompatActivity {

    //디비
    DiaryDao diaryDao;
    Diary user;
    DiaryDatabase database;
    Diary specificDiary;
    //불러온 날짜
    long specificDate;
    TextView todayText;
    TextView diaryTextView;
    TextView tagTextView;
    ImageView imageView;
    ImageButton editButton;
    SharedPreferences sharedPref, sp2;
    StringBuffer toDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initSharedPreferences();
        initFontStyle();
        setContentView(R.layout.activity_read_diary);
        initDataBase();
        initViews();
        initDiary();
        initWroteDiary();
        initFontSize();
        initListener();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    private void initListener() {
        editButton.setOnClickListener(v -> {
            Intent edit = new Intent(ReadDiaryActivity.this, EditDiaryActivity.class);
            edit.putExtra("checkId", specificDate);
            startActivity(edit);
        });
    }

    private void initWroteDiary() {
        if (specificDiary != null) {
            setSelectedDate();
            todayText.setText(toDay);
            diaryTextView.setText(specificDiary.diaryText);
            tagTextView.setText("#" + specificDiary.diaryTag);
            if (specificDiary.image != null) {
                imageView.setImageBitmap(new BitmapConverter().stringToBitmap(specificDiary.image));
            }
        }
    }

    private void initDiary() {
        //에러가 발생한 경우
        if (specificDate == 0) {
            finish();
        }
        //해당 날짜의 일기를 불러오기
        Diary specificDiary = diaryDao.getSpecificDiary(specificDate);
        //해당 날짜에 일기를 쓰지 않은 경우
        if (specificDiary == null) {
            user.uid = specificDate;
            diaryDao.insertDiary(user);

        } else if (specificDiary.lockEntity) {
            Toast.makeText(this, "잠겨있습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initFontStyle() {
        if (sharedPref.getString("Font", "font1").equals("font1")) {
            setTheme(R.style.Theme_Font1);
        } else if (sharedPref.getString("Font", "font1").equals("font2")) {
            setTheme(R.style.Theme_Font2);
        } else if (sharedPref.getString("Font", "font1").equals("font3")) {
            setTheme(R.style.Theme_Font3);
        } else {
            setTheme(R.style.Theme_Font4);
        }
    }

    @SuppressLint("SetTextI18n")
    private void initViews() {

        //컴포넌트 초기화
        todayText = findViewById(R.id.todayText);
        diaryTextView = findViewById(R.id.DiaryTextView);
        tagTextView = findViewById(R.id.tagTextView);
        imageView = findViewById(R.id.imageView);
        editButton = findViewById(R.id.editButton);
        specificDate = getIntent().getLongExtra("checkId", 0);
        specificDiary = diaryDao.getSpecificDiary(specificDate);
    }

    private void setSelectedDate() {
        String LongTypeChange = Long.toString(specificDiary.uid);
        toDay = new StringBuffer(LongTypeChange);
        toDay.insert(4, "-");
        toDay.insert(7, "-");
    }

    private void initDataBase() {
        //DB 사용을 위해
        database = Room.databaseBuilder(getApplicationContext(), DiaryDatabase.class, "cat_diary")
                .fallbackToDestructiveMigration()       //스키마 버전 변경가능
                .allowMainThreadQueries()               //Main Thread에서 DB에 I/O를 가능
                .build();
        diaryDao = database.userDao(); //인터페이스 객체할당
    }

    private void initSharedPreferences() {
        sharedPref = getSharedPreferences("CatDiary", Context.MODE_PRIVATE);
        sharedPref = getSharedPreferences("Font", MODE_PRIVATE);
        sp2 = getSharedPreferences("FontSize", MODE_PRIVATE);
    }

    private void initFontSize() {
        if (sp2.getString("FontSize", "medium").equals("small")) {
            diaryTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.main_text_size_0));
            tagTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.main_text_size_0));
        } else if (sp2.getString("FontSize", "medium").equals("medium")) {
            diaryTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.main_text_size_1));
            tagTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.main_text_size_1));
        } else {
            diaryTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.main_text_size_2));
            tagTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.main_text_size_2));
        }
    }
}