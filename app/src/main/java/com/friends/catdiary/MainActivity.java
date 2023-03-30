package com.friends.catdiary;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.friends.catdiary.calendar.CalendarActivity;
import com.friends.catdiary.db.Diary;
import com.friends.catdiary.db.DiaryDao;
import com.friends.catdiary.db.DiaryDatabase;
import com.friends.catdiary.setting.SettingActivity;
import com.friends.catdiary.setting.ThemeUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


//주석을 자세히 읽어 주세요. 이해하기 편할 것입니다. 안 되면 물어봐주세요(20_박형준).
public class MainActivity extends AppCompatActivity {
    //왠만한 것들은 전역변수로 설정하는 것이 편리하다.

    //디비
    private DiaryDao diaryDao;
    DiaryDatabase database;
    Diary user;
    //오늘 날짜
    long curTime;
    //기본 비밀번호 값 변수명 바꿔줘야 할
    //오늘 날짜 값이며, DB의  주 식별자 키이다.
    TextView toDayText;
    //갤러리로 이동하는 버튼
    ImageButton toGallery;
    //설정 버튼
    ImageButton toSetting;
    //달력으로 이동하는 버튼
    ImageButton toCalendar;
    //즐겨찾기 버튼
    ImageButton checkFavorite;
    //선택된 사진을 확인하는 뷰
    ImageView isSelectedPicture;
    //잠금 설정
    ImageView LockImageButton;
    //저장 버튼
    Button saveButton;
    //일기 내용 적는 곳
    EditText diaryEditText;
    //태그 적는 곳
    EditText diaryTagText;

    //즐겨찾기 여부 확인 변수
    //기본값은 즐겨찾기 안한 상태로 false 부여
    Boolean isFavorite = false;

    //사진 넣었는지 안넣었는지
    Boolean isImage = false;
    //잠겨있는가를 물어본다.
    Boolean isLocked = false;
    //DB 태그를 저장하는 변수
    String diaryTag;
    //DB 일기 텍스트를 저장하는 변수
    String diaryText;
    //비밀번호를 저장하기 위해서 사용
    SharedPreferences sharedPref, sp1, sp2;
    //
    String password;

    //오늘날짜
    long dbTime;
    //선택한 이미지 Uri
    Uri selectedImage;
    //선택한 이미지 Bitmap
    Bitmap selectedBitmap;
    String themeColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initSharedPreferences();
        initFontStyle();
        initTheme();
        setContentView(R.layout.activity_main);
        initDataBase();
        initViews();

        setTodaysDate();
        initCurrentTime();
        initListener();
        initFontSize();
        initDiary();
        //테스트 값을 위한 함수
        //inputTestDataToDatabase();
    }

    private void initTheme() {
        themeColor = ThemeUtil.modLoad(getApplicationContext());
        ThemeUtil.applyTheme(themeColor);

    }

    private void initFontStyle() {
        if (sp1.getString("Font", "font1").equals("font1")) {
            setTheme(R.style.Theme_Font1);
        } else if (sp1.getString("Font", "font1").equals("font2")) {
            setTheme(R.style.Theme_Font2);
        } else if (sp1.getString("Font", "font1").equals("font3")) {
            setTheme(R.style.Theme_Font3);
        } else {
            setTheme(R.style.Theme_Font4);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        initSharedPreferences();
        setWriteDiaryText();
        initDatabaseTuple();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    //재시작해서 초기화한다. (개념을 자세히 몰라서 일단 다 넣었다.)
    @Override
    protected void onStart() {
        super.onStart();

        initDataBase();
        initViews();
        initCurrentTime();
        initListener();
        initDiary();
        initSharedPreferences();
    }


    private void initCurrentTime() {
        // String 형식의 날짜 data Db의 primary 동일한 data type 변경하기
        dbTime = Long.parseLong((new SimpleDateFormat("yyyyMMdd")).format(new Date(curTime)));
    }

    private void initDiary() {
        Diary todayDiary = diaryDao.getSpecificDiary(dbTime);
        if (todayDiary == null) return;
        setTodayDiaryAndTagText(todayDiary);
        setFavorite(todayDiary.star);
        setLock(todayDiary.lockEntity);
        setIsImage(todayDiary);
        if (isImage) {
            isSelectedPicture.setVisibility(View.VISIBLE);
            isSelectedPicture.setImageBitmap(new BitmapConverter().stringToBitmap(todayDiary.image));
        }
    }

    private void setTodayDiaryAndTagText(Diary todayDiary) {
        diaryEditText.setText(todayDiary.diaryText);
        diaryTagText.setText(todayDiary.diaryTag);
    }

    private void setIsImage(Diary todayDiary) {
        if (todayDiary.image != null) isImage = true;
        else isImage = false;
    }

    private void setLock(boolean currentLockState) {
        if (currentLockState) locked();
        else openTheLock();
    }

    private void locked() {
        LockImageButton.setImageResource(R.drawable.ic_lock_closed);
        isLocked = true;
    }

    private void openTheLock() {
        LockImageButton.setImageResource(R.drawable.ic_lock_open);
        isLocked = false;
    }

    private void setFavorite(boolean currentFavoriteState) {
        if (currentFavoriteState) {
            checkFavorite.setImageResource(R.drawable.ic_star_yellow);
            isFavorite = true;
        } else {
            checkFavorite.setImageResource(R.drawable.ic_star_black);
            isFavorite = false;
        }
    }

    private void initViews() {
        //컴포넌트 초기화
        toDayText = findViewById(R.id.todayText);
        toGallery = findViewById(R.id.toGalleryButton);
        toSetting = findViewById(R.id.toSettingButton);
        toCalendar = findViewById(R.id.toCalendarBtn);
        saveButton = findViewById(R.id.saveDiaryButton);
        diaryEditText = findViewById(R.id.writeDiaryEditText);
        diaryTagText = findViewById(R.id.writeDiaryEditTag);
        isSelectedPicture = findViewById(R.id.isSelectedPicture);
        checkFavorite = findViewById(R.id.checkFavoriteButton);
        LockImageButton = findViewById(R.id.checkLockButton);
        checkFavorite.setImageResource(R.drawable.ic_star_black);
    }

    private void setTodaysDate() {
        curTime = System.currentTimeMillis();
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
        String toDay = timeFormat.format(new Date(curTime));
        toDayText.setText(toDay);
    }

    private void initDataBase() {
        database = Room.databaseBuilder(getApplicationContext(), DiaryDatabase.class, "cat_diary")
                .fallbackToDestructiveMigration()       //스키마 버전 변경가능
                .allowMainThreadQueries()               //Main Thread에서 DB에 I/O를 가능
                .build();
        diaryDao = database.userDao(); //인터페이스 객체할당
    }

    private void initListener() {
        toCalendar.setOnClickListener(v -> {
            clickCalendarImageToCalendarActivity();
        });
        toSetting.setOnClickListener(v -> {
            clickSettingIconToSettingActivity();
        });
        toGallery.setOnClickListener(v -> {
            toGallery();
        });
        saveButton.setOnClickListener(v -> {
            clickSaveButton();
        });
        //사진을 클릭하면 사진이 지워진다.
        isSelectedPicture.setOnClickListener(v -> {
            deleteImage();
            isImage = false;
        });

        //즐겨찾기 버튼 리스너
        checkFavorite.setOnClickListener(v -> {
            clickFavoriteImage();
        });

        //잠금 버튼 리스너
        LockImageButton.setOnClickListener(v -> {
            checkLock();
        });
    }


    private void checkLock() {
        // 잠겨있을 때 클릭
        if (isLocked) {
            //사용자가 셰어드 프리퍼런스로 설정한 비밀번호 불러오기
            password = sharedPref.getString("password", "0000");
            Log.e("password", "mainPassword" + password);
            // 사용자가 비밀번호 입력하기 위한 EditText 작성
            EditText passwordEditText = new EditText(MainActivity.this);
            // 정수값만 입력 받을 수 있도록 설정하기
            passwordEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            passwordEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
            passwordEditText.setInputType(InputType.TYPE_CLASS_NUMBER
                    | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            passwordEditText.setHint("초기 비밀번호:0000");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("비밀번호 입력")   // AlertDialog Title
                    .setView(passwordEditText)  // Into AlertDialog my Edittext
                    // 사용자가 확인 버튼 눌렀을 때
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String user_input_password = passwordEditText.getText().toString();
                            // 비번 일치
                            if (user_input_password.equals(password)) {
                                openTheLock();
                                putShortToastMessage("잠금이 해제되었습니다.");
                            } else {
                                //불일치
                                locked();
                                putShortToastMessage("비밀번호가 일치하지 않습니다.");
                            }
                        }
                    })
                    // 사용자가 취소 버튼 눌렀을 때
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            putShortToastMessage("취소했습니다.");
                            locked();
                        }
                    });
            builder.create();
            builder.show();
        }
        //열려있으므로 잠군다.
        else {
            locked();
        }
    }


    private void clickFavoriteImage() {
        if (isFavorite) clickActivateFavorite();
        else clickDeactivateFavorite();
    }

    private void clickActivateFavorite() {
        checkFavorite.setImageResource(R.drawable.ic_star_black);
        isFavorite = false;
    }

    private void clickDeactivateFavorite() {
        checkFavorite.setImageResource(R.drawable.ic_star_yellow);
        isFavorite = true;
    }

    private void toGallery() {
        isSelectedPicture.setVisibility(View.VISIBLE);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
        Log.e("pic", "dd");
        startActivityForResult(intent, 1001);
    }

    private void initFontSize() {
        if (sp2.getString("FontSize", "medium").equals("small")) {
            diaryEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.main_text_size_0));
            diaryTagText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.main_text_size_0));
        } else if (sp2.getString("FontSize", "medium").equals("medium")) {
            diaryEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.main_text_size_1));
            diaryTagText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.main_text_size_1));
        } else {
            diaryEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.main_text_size_2));
            diaryTagText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.main_text_size_2));
        }
    }

    private void clickSettingIconToSettingActivity() {
        Intent set = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(set);
    }

    private void clickCalendarImageToCalendarActivity() {
        Intent cal = new Intent(this, CalendarActivity.class);
        cal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        overridePendingTransition(0, 0);
        startActivity(cal);
        overridePendingTransition(0, 0);
    }

    private void clickSaveButton() {
        setWriteDiaryText();
        initDatabaseTuple();
        putShortToastMessage("일기가 저장되었습니다");
    }

    private void putShortToastMessage(String word) {
        Toast.makeText(this, word, Toast.LENGTH_SHORT).show();
    }

    private void setWriteDiaryText() {
        diaryText = diaryEditText.getText().toString();
        diaryTag = diaryTagText.getText().toString();
    }

    private void initDatabaseTuple() {
        //user 객체에 값을 넣는다.
        user = new Diary();
        Diary todayDiary = diaryDao.getSpecificDiary(dbTime);
        user.uid = dbTime;
        user.diaryText = diaryText;
        user.diaryTag = diaryTag;
        user.star = isFavorite;
        user.lockEntity = isLocked;
        if (isImage) user.image = todayDiary.image;
        diaryDao.insertDiary(user);
    }

    //deleteImage를 작성
    private void deleteImage() {
        isSelectedPicture.setVisibility(View.GONE);
    }


    //사진 관련 기능 구현한 메소드 db에 저장하기 위한 변환 과정
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == 1001) {

            if (data == null) return;

            selectedImage = data.getData();
            selectedBitmap = null;

            try {
                if (Build.VERSION.SDK_INT >= 29) {
                    ImageDecoder.Source src = ImageDecoder.createSource(getContentResolver(), selectedImage);
                    selectedBitmap = ImageDecoder.decodeBitmap(src);
                } else {
                    selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                }
            } catch (IOException ioException) {

            }

            if (selectedBitmap != null) {
                isImage = true;
                Log.e("selectPicture", "yes");

                Bitmap resizedBitmapImage = resizeBitmapImage(selectedBitmap, 500);
                Log.e("resizeBitmapImage", resizedBitmapImage.toString());
                isSelectedPicture.setImageBitmap(resizedBitmapImage);

                Diary todayDiary = diaryDao.getSpecificDiary(dbTime);
                todayDiary.image = new BitmapConverter().bitmapToString(selectedBitmap);
                diaryDao.insertDiary(todayDiary);
            }
        }
    }

    public Bitmap resizeBitmapImage(Bitmap source, int maxResolution) {
        int width = source.getWidth();
        int height = source.getHeight();
        int newWidth = width;
        int newHeight = height;
        float rate = 0.0f;

        if (width > height) {
            if (maxResolution < width) {
                rate = maxResolution / (float) width;
                newHeight = (int) (height * rate);
                newWidth = maxResolution;
            }
        } else {
            if (maxResolution < height) {
                rate = maxResolution / (float) height;
                newWidth = (int) (width * rate);
                newHeight = maxResolution;
            }
        }

        return Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
    }

    private void initSharedPreferences() {
        sharedPref = getSharedPreferences("CatDiary", Context.MODE_PRIVATE);
        sp1 = getSharedPreferences("Font", MODE_PRIVATE);
        sp2 = getSharedPreferences("FontSize", MODE_PRIVATE);
    }
}
