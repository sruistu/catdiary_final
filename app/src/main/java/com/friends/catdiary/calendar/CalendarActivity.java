package com.friends.catdiary.calendar;

import static android.graphics.Color.RED;
import static org.threeten.bp.DayOfWeek.MONDAY;
import static org.threeten.bp.DayOfWeek.SUNDAY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.friends.catdiary.BackKeyHandler;
import com.friends.catdiary.BitmapConverter;
import com.friends.catdiary.EditDiaryActivity;
import com.friends.catdiary.FavoritesActivity;
import com.friends.catdiary.R;
import com.friends.catdiary.ReadDiaryActivity;
import com.friends.catdiary.TagActivity;
import com.friends.catdiary.db.Diary;
import com.friends.catdiary.db.DiaryDao;
import com.friends.catdiary.db.DiaryDatabase;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateLongClickListener;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter;
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter;

import java.util.List;

public class CalendarActivity extends AppCompatActivity {
    //디비
    DiaryDao diaryDao;
    DiaryDatabase database;
    //TextView
    TextView ReadOnlyDiaryText;
    LinearLayout diaryTouch;

    Diary user;
    //CalendarView
    MaterialCalendarView materialCalendarView;
    //ImageButton
    ImageButton smallEdit;
    ImageButton lockButton;
    ImageButton favorites;
    //태그 텍스트
    TextView ReadOnlyTagText;
    //즐겨찾기 버튼
    ImageButton favoriteButton;
    //태그검색 버튼
    ImageButton searchButton;
    //이미지를 가져온다.
    ImageView putPicture;
    //빨간 점 찍는 eventDecorator
    EventDecorator eventDecorator = new EventDecorator(RED);
    //캘린더의 클릭한 날짜를 저장하는 변수
    Long selectedDate;
    Long todayDate;
    //비밀 번호를 저장하는 변수
    String password;
    //비밀 번호 저장을 위해서 사용
    SharedPreferences sharedPref, sharedPref2, sharedPref3;

    private final BackKeyHandler backKeyHandler = new BackKeyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initSharedPreferences();
        initFontStyle();
        setContentView(R.layout.activity_calender);
        initDataBase();
        initView();
        initFontSize();
        initMaterialCalendar();
        initListener();
        //일기가 저장되어 있는 날짜 표시
        allWriteDaysCheck();
        //즐겨찾기 표시
        allFavoriteDaysCheck();
    }

    //    화면이 다시 보일때마다 decorators 재시작
    @Override
    protected void onResume() {
        super.onResume();
//        initSharedPreferences();
//        initFontStyle();
//        setContentView(R.layout.activity_calender);
//        initDataBase();
//        initView();
//        initFontSize();
//        initMaterialCalendar();
//        initListener();

        materialCalendarView.removeDecorators();
        allWriteDaysCheck();
        materialCalendarView.addDecorator(eventDecorator);
        allFavoriteDaysCheck();
        setSpecificDiary(selectedDate);
    }

    private void initFontSize() {
        //폰트 사이즈 가져와서 smallText 변경
        if (sharedPref2.getString("FontSize", "medium").equals("small")) {
            ReadOnlyDiaryText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.main_text_size_0));
        } else if (sharedPref2.getString("FontSize", "medium").equals("medium")) {
            ReadOnlyDiaryText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.main_text_size_1));
        } else {
            ReadOnlyDiaryText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.main_text_size_2));
        }
    }

    private void initFontStyle() {
        if (sharedPref3.getString("Font", "font1").equals("font1")) {
            setTheme(R.style.Theme_Font1);
        } else if (sharedPref3.getString("Font", "font1").equals("font2")) {
            setTheme(R.style.Theme_Font2);
        } else if (sharedPref3.getString("Font", "font1").equals("font3")) {
            setTheme(R.style.Theme_Font3);
        } else {
            setTheme(R.style.Theme_Font4);
        }
    }

    private void initMaterialCalendar() {
        //decorator 추가
        materialCalendarView.addDecorator(eventDecorator);

        //캘린더 기본 설정
        materialCalendarView.state().edit().setFirstDayOfWeek(SUNDAY).commit();

        // 월, 요일을 한글로 보이게 설정
        materialCalendarView.setTitleFormatter(new MonthArrayTitleFormatter(getResources().getTextArray(R.array.custom_months)));
        materialCalendarView.setWeekDayFormatter(new ArrayWeekDayFormatter(getResources().getTextArray(R.array.custom_weekdays)));

        materialCalendarView.setDateTextAppearance(R.style.CustomDateTextAppearance);
        materialCalendarView.setWeekDayTextAppearance(R.style.CustomWeekDayAppearance);
        materialCalendarView.setHeaderTextAppearance(R.style.CustomHeaderTextAppearance);
    }

    private void initSharedPreferences() {
        sharedPref = getSharedPreferences("CatDiary", Context.MODE_PRIVATE);
        sharedPref3 = getSharedPreferences("Font", MODE_PRIVATE);
        sharedPref2 = getSharedPreferences("FontSize", MODE_PRIVATE);
    }

    private void initListener() {
        //오늘 날짜의 일기 불러오기
        todayDate = getDBDate(CalendarDay.today());
        materialCalendarView.setSelectedDate(CalendarDay.today());
        setSpecificDiary(todayDate);

        //현재 클릭한 날짜의 정보를 ReadDiaryActivity에 전달해줘야 한다.
        diaryTouch.setOnClickListener(v -> {
            moveToReadDiaryActivity();
        });

        searchButton.setOnClickListener(v -> {
            moveToTagActivity();
        });

        smallEdit.setOnClickListener(v -> {
            moveToEditDiaryActivity();
        });

        favorites.setOnClickListener(v -> {
            moveToFavoritesActivity();
        });

        materialCalendarView.setOnDateLongClickListener(new OnDateLongClickListener() {
            @Override
            public void onDateLongClick(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CalendarActivity.this);
                builder.setTitle("선택한 일기를 삭제합니다.")  // AlertDialog Title

                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                diaryDao.deleteDate(selectedDate);
                                Toast.makeText(CalendarActivity.this, "삭제완료", Toast.LENGTH_SHORT).show();
                                materialCalendarView.removeDecorators(); //decorator 모두 지우기
                                allWriteDaysCheck(); //일기 쓴 날짜 decorator
                                allFavoriteDaysCheck(); //즐겨찾기 한 날짜 decorator
                                materialCalendarView.addDecorator(eventDecorator); //오늘 날짜 점 찍기 decorator
                                setDiaryAndTagText(diaryDao.getSpecificDiary(selectedDate));
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(CalendarActivity.this, "취소했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                builder.create();
                builder.show();
            }
        });

        //캘린더 클릭했을 때 실행되는 리스너
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                //db검색에 활용하기 위해 CalendarDay를 Long형식으로 변환
                Long dbDate = getDBDate(date);

                //DB에서 클릭한 날짜에 대한 일기 불러오기
                setSpecificDiary(dbDate);
            }
        });

        //즐겨찾기 버튼 갱신 리스너
        favoriteButton.setOnClickListener(v -> {
            //해당 날짜의 일기 정보 가져오기
            Diary specificDiary = diaryDao.getSpecificDiary(selectedDate);
            //예외처리
            if (specificDiary == null) {
                return;
            }

            //해당 날짜의 일기를 즐겨찾기 한 경우
            if (specificDiary.star) {
                favoriteButton.setImageResource(R.drawable.ic_star_black);
                specificDiary.star = false;

            }

            //해당 날짜의 일기를 즐겨찾기 안 한 경우
            else {
                favoriteButton.setImageResource(R.drawable.ic_star_yellow);
                specificDiary.star = true;
            }

            //변경한 즐겨찾기 정보를 담은 일기를 다시 DB에 집어넣기
            diaryDao.insertDiary(specificDiary);

            materialCalendarView.removeDecorators(); //decorator 모두 지우기
            allWriteDaysCheck(); //일기 쓴 날짜 decorator
            materialCalendarView.addDecorator(eventDecorator); //오늘 날짜 점 찍기 decorator
            allFavoriteDaysCheck(); //즐겨찾기 한 날짜 decorator
        });

        //잠금 버튼 갱신 리스너
        lockButton.setOnClickListener(v -> {
            //해당 날짜의 일기 정보 가져오기
            Diary specificDiary = diaryDao.getSpecificDiary(selectedDate);

            //예외처리
            if (specificDiary == null) {
                return;
            }

            // 사용자가 비밀번호 입력하기 위한 EditText 작성
            EditText passwordEditText = new EditText(this);
            // 정수값만 입력 받을 수 있도록 설정하기
            passwordEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
            passwordEditText.setInputType(InputType.TYPE_CLASS_NUMBER
                    | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            //DB에저장된 lock값이 True면 잠김 이미지
            if (specificDiary.lockEntity) {
                //사용자가 셰어드 프리퍼런스로 설정한 비밀번호 불러오기
                password = sharedPref.getString("password", "0000");

                //힌트 값을 추가한다.
                passwordEditText.setHint("초기 비밀번호:0000");
                // make AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("비밀번호 입력")   // AlertDialog Title
                        .setView(passwordEditText)  // Into AlertDialog my Edittext
                        // 사용자가 확인 버튼 눌렀을 때
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String user_input_password = passwordEditText.getText().toString();
                                // 셰어드 프리퍼런스의 비밀번호와 EditText에 입력한 비밀번호가 일치하는지 확인
                                if (user_input_password.equals(password)) {
                                    //일치하면 풀어준다.
                                    lockButton.setImageResource(R.drawable.ic_lock_open);
                                    specificDiary.lockEntity = false;
                                    Toast.makeText(CalendarActivity.this, "잠금이 해제되었습니다.", Toast.LENGTH_SHORT).show();
                                    //변경한 잠김 정보를 담은 일기를 다시 DB에 집어넣기
                                    diaryDao.insertDiary(specificDiary);
                                    checkLocked(specificDiary); //텍스트뷰 갱신

                                } else {
                                    //일치하지 않으면 잠금 풀어주지 않는다.
                                    lockButton.setImageResource(R.drawable.ic_lock_closed);
                                    specificDiary.lockEntity = true;
                                    Toast.makeText(CalendarActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                                    //변경한 잠김 정보를 담은 일기를 다시 DB에 집어넣기
                                    diaryDao.insertDiary(specificDiary);
                                }
                            }
                        })
                        // 사용자가 취소 버튼 눌렀을 때
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(CalendarActivity.this, "취소했습니다.", Toast.LENGTH_SHORT).show();
                                lockButton.setImageResource(R.drawable.ic_lock_closed);
                                specificDiary.lockEntity = true;
                                //변경한 잠김 정보를 담은 일기를 다시 DB에 집어넣기
                                diaryDao.insertDiary(specificDiary);
                            }
                        });
                lockButton.setImageResource(R.drawable.ic_lock_closed);
                specificDiary.lockEntity = true;
                //변경한 잠김 정보를 담은 일기를 다시 DB에 집어넣기
                diaryDao.insertDiary(specificDiary);
                builder.create();
                builder.show();

            } else {
                //DB에저장된 lock값이 False면 열린 이미지
                lockButton.setImageResource(R.drawable.ic_lock_closed);
                specificDiary.lockEntity = true;
                //변경한 즐겨찾기 정보를 담은 일기를 다시 DB에 집어넣기
                diaryDao.insertDiary(specificDiary);
                checkLocked(specificDiary);  //텍스트뷰 갱신
            }

        });

    }

    private void moveToReadDiaryActivity() {
        Intent cal = new Intent(CalendarActivity.this, ReadDiaryActivity.class);
        if (diaryDao.getSpecificDiary(selectedDate) == null) {
            Toast.makeText(CalendarActivity.this, "작성된 일기가 없습니다", Toast.LENGTH_LONG).show();
            return;
        }
        cal.putExtra("checkId", selectedDate);
        startActivity(cal);
    }

    private void moveToTagActivity() {
        Intent tag = new Intent(CalendarActivity.this, TagActivity.class);
        startActivity(tag);
    }

    private void moveToFavoritesActivity() {
        Intent fav = new Intent(CalendarActivity.this, FavoritesActivity.class);
        startActivity(fav);
    }

    private void moveToEditDiaryActivity() {
        Intent edit = new Intent(CalendarActivity.this, EditDiaryActivity.class);
        if (diaryDao.getSpecificDiary(selectedDate) == null) {
            Diary user = new Diary();
            user.uid = selectedDate;
            user.diaryText = "";
            user.diaryTag = "";
            diaryDao.insertDiary(user);
        } else if (diaryDao.getSpecificDiary(selectedDate).lockEntity) {
            Toast.makeText(CalendarActivity.this, "잠겨있습니다", Toast.LENGTH_SHORT).show();
            return;
        }
        edit.putExtra("checkId", selectedDate);
        startActivity(edit);
    }

    //현재의 날짜를 DB의 데이터 타입인 long형식으로 변환한다.
    private Long getDBDate(CalendarDay date) {
        int year = date.getYear();

        int month = date.getMonth();
        String dbMonth = "";
        if (month < 10) dbMonth = "0" + month;
        else dbMonth = month + "";

        int day = date.getDay();
        String dbDay = "";
        if (day < 10) dbDay = "0" + day;
        else dbDay = day + "";

        return Long.parseLong(year + dbMonth + dbDay);
    }

    private void initView() {
        //스몰텍스트 뷰 불러오기
        ReadOnlyDiaryText = findViewById(R.id.DayDiary);
        //캘린더 뷰 불러오기
        materialCalendarView = findViewById(R.id.calendarView);
        //스몰텍스트 뷰 불러오기
        smallEdit = findViewById(R.id.smallEdit);
        //아래의 태그를 불러오기
        ReadOnlyTagText = findViewById(R.id.smallTag);
        //즐겨찾기 뷰 불러오기
        favoriteButton = findViewById(R.id.smallStar);
        //이미지 뷰 불러오기
        putPicture = findViewById(R.id.showPicture);
        //자물쇠 뷰 불러오기
        lockButton = findViewById(R.id.smallLock);
        //태그검색버튼 뷰 불러오기
        searchButton = findViewById(R.id.searchTag);

        diaryTouch = findViewById(R.id.diaryClick);

        favorites = findViewById(R.id.favorites);

    }

    private void initDataBase() {
        //DB 사용을 위해
        database = Room.databaseBuilder(getApplicationContext(), DiaryDatabase.class, "cat_diary").fallbackToDestructiveMigration()       //스키마 버전 변경가능
                .allowMainThreadQueries()               //Main Thread에서 DB에 I/O를 가능
                .build();
        diaryDao = database.userDao(); //인터페이스 객체할당
    }

    @SuppressLint("SetTextI18n")
    private void setSpecificDiary(Long dbDate) {
        //해당 날짜의 다이어리 불러오기
        Diary specificDiary = diaryDao.getSpecificDiary(dbDate);
        //선택한 달력의 날짜를 얻어온다.
        selectedDate = dbDate;
        checkWroteDiary(specificDiary);
    }

    private void checkWroteDiary(Diary specificDiary) {
        setDiaryAndTagText(specificDiary);
        setSelectedPicture(specificDiary);
        setImageButton(specificDiary);
    }

    private void setDiaryAndTagText(Diary specificDiary) {

        if (specificDiary != null) {
            ReadOnlyDiaryText.setText(specificDiary.diaryText);
            ReadOnlyTagText.setText("#" + specificDiary.diaryTag);
        } else {
            ReadOnlyDiaryText.setText("No Diary!!");
            ReadOnlyTagText.setText("No Tag!!");
        }
    }

    private void setSelectedPicture(Diary specificDiary) {
        if (specificDiary == null) {
            putPicture.setImageResource(0);
        } else if (specificDiary.image != null) {
            putPicture.setImageBitmap(new BitmapConverter().stringToBitmap(specificDiary.image));
        } else {
            putPicture.setImageResource(0);
        }
    }

    private void setImageButton(Diary specificDiary) {
        setFavoriteButton(specificDiary);
        checkLocked(specificDiary);
    }

    private void setFavoriteButton(Diary specificDiary) {
        if (specificDiary == null) {
            favoriteButton.setImageResource(R.drawable.ic_star_black);
        } else if (specificDiary.star) {
            favoriteButton.setImageResource(R.drawable.ic_star_yellow);
        } else favoriteButton.setImageResource(R.drawable.ic_star_black);
    }

    private void checkLocked(Diary specificDiary) {
        if (specificDiary == null) {
            lockButton.setImageResource(R.drawable.ic_lock_open);
        } else {
            if (specificDiary.lockEntity) {
                ReadOnlyDiaryText.setText("잠겨있습니다.");
                ReadOnlyTagText.setText("잠겨있습니다.");
                setSelectedPicture(null);
                lockButton.setImageResource(R.drawable.ic_lock_closed);
            } else {
                setDiaryAndTagText(specificDiary);
                setSelectedPicture(specificDiary);
                lockButton.setImageResource(R.drawable.ic_lock_open);
            }
        }
    }

    //데이터베이스에서 즐겨찾기 한 날짜를 모두 불러와서 노랗게 표현하는 함수
    private void allFavoriteDaysCheck() {
        //1. 데이터베이스에서 즐겨찾기 된 날짜 모두 불러오기
        List<Diary> allFavoriteDiary = getAllFavoriteDayByDatabase();

        //2. 모든 날짜에 대해 변겅
        for (Diary diary : allFavoriteDiary) {
            checkFavoriteDay(diary);
        }
    }

    //데이터베이스에서 즐겨찾기 된 날짜 모두 불러오는 함수
    private List<Diary> getAllFavoriteDayByDatabase() {
        return diaryDao.getFavoriteAllDays();
    }

    //전달된 날짜값... 노랗게 표현하는 함수
    private void checkFavoriteDay(Diary diary) {
        //Long형식의 날짜값에서 년, 월, 일 가져오기
        int year = (int) (diary.uid / 10000);
        int month = (int) ((diary.uid % 10000) / 100);
        int day = (int) (diary.uid % 100);

        //해당되는 년,월,일 날짜에 oneDayDecorator 이용하여 노랗게 변경
        OneDayDecorator oneDayDecorator = new OneDayDecorator(CalendarDay.from(year, month, day), getResources().getColor(R.color.Favorites));
        materialCalendarView.addDecorator(oneDayDecorator);
    }

    //저장된 모든 날짜 불러와서 진하게 표시하는 함수
    private void allWriteDaysCheck() {
        List<Diary> allWriteDay = getAllWriteDayByDatabase();

        for (Diary diary : allWriteDay) {
            checkAllDay(diary);
        }
    }

    //데이터베이스에 일기가 저장된 날짜 모두 불러오는 함수
    private List<Diary> getAllWriteDayByDatabase() {
        return diaryDao.getAllWriteDays();
    }

    //전달된 날짜값 진하게 표현 (캘린더 날짜 기본색을 옅게 설정했다)
    private void checkAllDay(Diary diary) {
        int year = (int) (diary.uid / 10000);
        int month = (int) ((diary.uid % 10000) / 100);
        int day = (int) (diary.uid % 100);

        //해당되는 년, 월, 일 날짜에 noneDayDecorator 이용하여 변경
        NoneDayDecorator noneDayDecorator = new NoneDayDecorator(CalendarDay.from(year, month, day), getResources().getColor(R.color.OnBackground));
        materialCalendarView.addDecorator(noneDayDecorator);
    }

    @Override
    public void onBackPressed() {
        backKeyHandler.onBackPressed("두 번 누르면 종료됩니다.", 1);
    }
}