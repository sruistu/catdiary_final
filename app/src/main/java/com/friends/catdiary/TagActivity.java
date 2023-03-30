package com.friends.catdiary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.friends.catdiary.calendar.CalendarActivity;
import com.friends.catdiary.db.Diary;
import com.friends.catdiary.db.DiaryDao;
import com.friends.catdiary.db.DiaryDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TagActivity extends AppCompatActivity {

    //디비
    private DiaryDao diaryDao;
    DiaryDatabase database;

    SharedPreferences sp, sharedPref;

    EditText searchTag;
    ImageButton searchBtn;
    String[] tagList;
    String etText, count;

    ListView tagListView;
    ListViewAdapter adapter;
    List<String> listTag, listTagPre, titleList, secondList, listCount;
    private ArrayList<ListViewAdapter.ListViewItem> listViewItemList = new ArrayList<ListViewAdapter.ListViewItem>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

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

        initDataBase();
        initSharedPreferences();
        initView();
        initListener();

    }

    private void initView() {
        //잘 작동하는지 로그로 확인한다.
        Log.e("delete", "del");

        tagListView = (ListView) findViewById(R.id.tagListView);
        searchBtn = (ImageButton) findViewById(R.id.tagSearchButton);
        searchTag = (EditText) findViewById(R.id.tagSearchEditText);

        adapter = new ListViewAdapter();
        listTag = new ArrayList<>();
        listTagPre = new ArrayList<>();
        secondList = new ArrayList<>();
        listCount = new ArrayList<>();

        // db의 tag들을 불러옴
        titleList = diaryDao.getAllTag();
        tagList = titleList.toArray(new String[titleList.size()]);

        for (int i = 0; i < tagList.length; i++) {
            if (tagList[i].length() != 0) {
                listTagPre.add(tagList[i]);
                secondList.add(tagList[i]);
            } else {
                listTagPre.add(" 태그 없음...");
                secondList.add(" 태그 없음...");
            }
        }

        Set<String> set = new HashSet<String>(listTagPre);
        for (String str : set) {
            count = String.valueOf(Collections.frequency(listTagPre, str));
            listCount.add(count);
            listTag.add(str);
        }


        tagListView.setAdapter(adapter);
        for (int i = 0; i < listTag.size(); i++) {
            adapter.addItem("#" + listTag.get(i), listCount.get(i));

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

    private void initListener() {
        // 터치 이벤트시 에딧값을 받아와 search_tag 실행
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etText = searchTag.getText().toString();
                search_tag(etText);
            }
        });

        tagListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String str = listTag.get(i);

                Intent cal = new Intent(TagActivity.this, SearchResultActivity.class);
                cal.putExtra("tag", str);
                startActivity(cal);

            }
        });

    }

    private void initSharedPreferences() {
        sharedPref = getSharedPreferences("CatDiary", Context.MODE_PRIVATE);
    }

    private void search_tag(String charText) {
        // 리스트에 새값을 띄우기위해 값을 초기화해준다
        listViewItemList.clear();
        listTag.clear();
        listTagPre.clear();
        listCount.clear();

        if (charText.length() == 0) {     // 아무것도 검색하지 않았을때
            for (int i = 0; i < tagList.length; i++) {
                if (tagList[i].length() != 0) {
                    listTagPre.add(tagList[i]);
                } else {
                    listTagPre.add(" 태그 없음...");
                }
            }
        }
        // 문자 입력을 할때..
        else {
            // 리스트의 모든 데이터를 검색한다.
            for (int i = 0; i < secondList.size(); i++) {
                // list_sc의 모든 데이터에 입력받은 단어가 포함되어 있으면 true를 반환한다.
                if (secondList.get(i).toLowerCase().contains(charText)) {
                    // 검색된 데이터를 리스트에 추가한다.
                    listTagPre.add(secondList.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        Set<String> set = new HashSet<String>(listTagPre);
        for (String str : set) {
            count = String.valueOf(Collections.frequency(listTagPre, str));
            listCount.add(count);
            listTag.add(str);
        }

        tagListView.setAdapter(adapter);
        for (int i = 0; i < listTag.size(); i++) {
            adapter.addItem("#" + listTag.get(i), listCount.get(i));
        }
    }


    // 리스트뷰 커스텀을 위한 BaseAdapter 선언
    public class ListViewAdapter extends BaseAdapter {
        // Adapter에 추가된 데이터를 저장하기 위한 ArrayList


        // ListViewAdapter의 생성자
        public ListViewAdapter() {

        }

        // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
        @Override
        public int getCount() {
            return listViewItemList.size();
        }

        // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Context context = parent.getContext();

            // "listview_item" Layout을 inflate하여 convertView 참조 획득.
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.items, parent, false);
            }

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            TextView titleTextView = (TextView) convertView.findViewById(R.id.tagName);
            TextView descTextView = (TextView) convertView.findViewById(R.id.tagCount);

            // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
            ListViewItem listViewItem = listViewItemList.get(position);

            // 아이템 내 각 위젯에 데이터 반영
            titleTextView.setText(listViewItem.getTag());
            descTextView.setText(listViewItem.getDay());

            return convertView;
        }

        // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
        @Override
        public long getItemId(int position) {
            return position;
        }

        // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
        @Override
        public Object getItem(int position) {
            return listViewItemList.get(position);
        }

        // 아이템 데이터 추가를 위한 함수.
        public void addItem(String tag, String count) {
            ListViewItem item = new ListViewItem();

            item.getTag(tag);
            item.setDay(count);

            listViewItemList.add(item);


        }

        public class ListViewItem {
            private String tagStr;
            private String dayStr;

            public void getTag(String title) {
                tagStr = title;
            }

            public void setDay(String desc) {
                dayStr = desc;
            }

            public String getTag() {
                return this.tagStr;
            }

            public String getDay() {
                return this.dayStr;
            }
        }

    }
}
