package com.friends.catdiary.setting;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.room.Room;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.friends.catdiary.R;
import com.friends.catdiary.db.DiaryDao;
import com.friends.catdiary.db.DiaryDatabase;

public class SettingActivity extends AppCompatActivity {

    //디비
    private DiaryDao diaryDao;
    DiaryDatabase database;
    Button resetBtn;
    //
    Button changePassword;
    SwitchCompat darkMode;
    String themeColor;
    Button chFont;
    SharedPreferences sharedPref, sp, sp2;
    String currentPassword;
    String userInputCurrentPassword;
    String userInputNewPassword;

    //푸쉬알림
    EditText etToken;
    SwitchCompat push;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSharedPreferences();
        initFontStyle();
        setContentView(R.layout.activity_setting);
        initDataBase();
        initView();
        initAlarm();
        initListener();


    }

    private void initAlarm() {
        //        push = findViewById(R.id.push);
//        etToken = findViewById(R.id.etToken);
//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//
//                        try {
//                            if (!task.isSuccessful()) {
//                                System.out.println("Fetching FCM registration token failed");
//                                return;
//                            }
//
//                            // Get new FCM registration token
//                            String token = task.getResult();
//
//                            // Log and toast
//
//                            System.out.println(token);
//                            Toast.makeText(SettingActivity.this, "Your device registration token is" + token, Toast.LENGTH_SHORT).show();
//
//                            etToken.setText(token);
//                        }
//                        catch (Exception e){}
//                            Toast.makeText(SettingActivity.this, "dddd", Toast.LENGTH_SHORT).show();
//
//                    }
//                });
    }

    private void initFontStyle() {
        if (sp.getString("Font", "font1").equals("font1")) {
            setTheme(R.style.Theme_Font1);
        } else if (sp.getString("Font", "font1").equals("font2")) {
            setTheme(R.style.Theme_Font2);
        } else if (sp.getString("Font", "font1").equals("font3")) {
            setTheme(R.style.Theme_Font3);
        } else {
            setTheme(R.style.Theme_Font4);
        }
    }

    private void initDataBase() {
        //DB 사용할 경우 모든 Class에 넣어줘야한다.
        database = Room.databaseBuilder(getApplicationContext(), DiaryDatabase.class, "cat_diary")
                .fallbackToDestructiveMigration()       //스키마 버전 변경가능
                .allowMainThreadQueries()               //Main Thread에서 DB에 I/O를 가능
                .build();
        diaryDao = database.userDao(); //인터페이스 객체할당
    }

    private void initView() {
        resetBtn = findViewById(R.id.resetBtn);
        changePassword = findViewById(R.id.changePassword);
        chFont = findViewById(R.id.fontChange);
        darkMode = findViewById(R.id.darkMode);
    }

    private void initListener() {
        currentPassword = sharedPref.getString("password", "0000");


        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText passwordEditText = new EditText(SettingActivity.this);
                passwordEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4) });
                passwordEditText.setInputType(InputType.TYPE_CLASS_NUMBER
                        |InputType.TYPE_NUMBER_FLAG_DECIMAL);
                passwordEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("비밀번호 입력")
                        .setView(passwordEditText)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String userInputCurrentPassword = passwordEditText.getText().toString();
                                if(currentPassword.equals(userInputCurrentPassword)){

                                    Toast.makeText(SettingActivity.this, "삭제했습니다.", Toast.LENGTH_SHORT).show();
                                    diaryDao.deleteAll();
                                }
                                else Toast.makeText(SettingActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(SettingActivity.this, "취소했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                builder.create();
                builder.show();
            }
        });



        //글씨체 변경
        chFont.setOnClickListener(v -> {
            Intent changeFont = new Intent(SettingActivity.this, ChangeFontActivity.class);
            startActivity(changeFont);
        });

        //다크모드 스위치
        darkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonview, boolean isChecked) {
                SharedPreferences.Editor editor = sp2.edit();
                if (isChecked) {
                    themeColor = ThemeUtil.DARK_MODE;
                    ThemeUtil.applyTheme(themeColor);
                    ThemeUtil.modSave(getApplicationContext(), themeColor);
                    editor.putString("darkMode", "true");
                    editor.apply();
                } else {
                    themeColor = ThemeUtil.LIGHT_MODE;
                    ThemeUtil.applyTheme(themeColor);
                    ThemeUtil.modSave(getApplicationContext(), themeColor);
                    editor.putString("darkMode", "false");
                    editor.apply();
                }
            }
        });
        //다크모드 여부 불러와서 스위치 상태 조정
        
        if (sp2.getString("darkMode", "false").equals("true")) {
            darkMode.setChecked(true);
        } else {
            darkMode.setChecked(false);
        }


        //비밀번호 변경
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getLayoutInflater();
                View viewTest = inflater.inflate(R.layout.password_check_view, null);
                final EditText nowPassword = viewTest.findViewById(R.id.nowPassword);
                final EditText newPassword = viewTest.findViewById(R.id.newPassword);

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("비밀번호 입력")
                        .setView(viewTest)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                    userInputCurrentPassword = nowPassword.getText().toString();
                                    userInputNewPassword = newPassword.getText().toString();
                                    if(currentPassword.equals(userInputCurrentPassword)){
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        Toast.makeText(SettingActivity.this, "변경되었습니다.", Toast.LENGTH_LONG).show();
                                        editor.putString("password", userInputNewPassword);
                                        editor.apply();
                                        Log.e("password", "setting"+currentPassword);
                                    }
                                 else {
                                        Toast.makeText(SettingActivity.this, "변경되지 않았습니다.", Toast.LENGTH_LONG).show();
                                        Log.e("password", "setting"+currentPassword);
                                    }
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.e("password", "setting"+currentPassword);
                                Toast.makeText(SettingActivity.this, "취소했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                builder.create();
                builder.show();
            }
        });


    }


    private void initSharedPreferences() {
        sharedPref = getSharedPreferences("CatDiary", Context.MODE_PRIVATE);
        sp = getSharedPreferences("Font", MODE_PRIVATE);
        sp2 = getSharedPreferences("darkMode", MODE_PRIVATE);
    }
}