package com.friends.catdiary.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.friends.catdiary.R;

public class ChangeFontActivity extends AppCompatActivity {

    RadioButton btn1, btn2, btn3, btn4;
    RadioGroup rg1;
    TextView textView, sizeText;
    EditText editText;
    SeekBar seekBar;
    SharedPreferences sp, sp2;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getSharedPreferences("Font", MODE_PRIVATE);
        if (sp.getString("Font", "font1").equals("font1")) {
            setTheme(R.style.Theme_Font1);
        } else if (sp.getString("Font", "font1").equals("font2")) {
            setTheme(R.style.Theme_Font2);
        } else if (sp.getString("Font", "font1").equals("font3")) {
            setTheme(R.style.Theme_Font3);
        } else {
            setTheme(R.style.Theme_Font4);
        }

        setContentView(R.layout.activity_change_font);

        rg1 = findViewById(R.id.RG1);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        textView = findViewById(R.id.textView);
        sizeText = findViewById(R.id.sizeText);
        editText = findViewById(R.id.editView);
        seekBar = findViewById(R.id.textSizeSeekBar);

        rg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                SharedPreferences.Editor editor = sp.edit();
                switch (checkedId) {
                    case R.id.btn1:
                        editor.putString("Font", "font1");
                        editor.apply();
                        try {
                            Intent intent = getIntent();
                            finish(); //현재 액티비티 종료 실시
                            overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                            startActivity(intent); //현재 액티비티 재실행 실시
                            overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.btn2:
                        editor.putString("Font", "font2");
                        editor.apply();
                        try {
                            Intent intent = getIntent();
                            finish(); //현재 액티비티 종료 실시
                            overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                            startActivity(intent); //현재 액티비티 재실행 실시
                            overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.btn3:
                        editor.putString("Font", "font3");
                        editor.apply();
                        try {
                            Intent intent = getIntent();
                            finish(); //현재 액티비티 종료 실시
                            overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                            startActivity(intent); //현재 액티비티 재실행 실시
                            overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.btn4:
                        editor.putString("Font", "font4");
                        editor.apply();
                        try {
                            Intent intent = getIntent();
                            finish(); //현재 액티비티 종료 실시
                            overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                            startActivity(intent); //현재 액티비티 재실행 실시
                            overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 아무것도안함
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 아무것도안함
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = sp2.edit();
                switch (seekBar.getProgress()) {
                    case 0:
                        editor.putString("FontSize", "small");
                        editor.apply();
                        try {
                            Intent intent = getIntent();
                            finish(); //현재 액티비티 종료 실시
                            overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                            startActivity(intent); //현재 액티비티 재실행 실시
                            overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        editor.putString("FontSize", "medium");
                        editor.apply();
                        try {
                            Intent intent = getIntent();
                            finish(); //현재 액티비티 종료 실시
                            overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                            startActivity(intent); //현재 액티비티 재실행 실시
                            overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        editor.putString("FontSize", "large");
                        editor.apply();
                        try {
                            Intent intent = getIntent();
                            finish(); //현재 액티비티 종료 실시
                            overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                            startActivity(intent); //현재 액티비티 재실행 실시
                            overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });

        sp2 = getSharedPreferences("FontSize", MODE_PRIVATE);
        if (sp2.getString("FontSize", "medium").equals("small")) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.main_text_size_0));
            editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.main_text_size_0));
            seekBar.setProgress(0);
            sizeText.setText("글씨 크기 : 작음");

        } else if (sp2.getString("FontSize", "medium").equals("medium")) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.main_text_size_1));
            editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.main_text_size_1));
            seekBar.setProgress(1);
            sizeText.setText("글씨 크기 : 중간");
        } else {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.main_text_size_2));
            editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.main_text_size_2));
            seekBar.setProgress(2);
            sizeText.setText("글씨 크기 : 큼");
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ChangeFontActivity.this, SettingActivity.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //인텐트 플래그 설정
        startActivity(intent);  //인텐트 이동
        finish();   //현재 액티비티 종료
    }
}