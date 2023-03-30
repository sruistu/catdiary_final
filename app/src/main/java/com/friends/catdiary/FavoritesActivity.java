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

import com.friends.catdiary.db.DiaryDao;
import com.friends.catdiary.db.DiaryDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FavoritesActivity extends AppCompatActivity {

    //디비
    private DiaryDao diaryDao;
    DiaryDatabase database;

    SharedPreferences sp, sharedPref;

    String count;

    ListView favoritesListView;
    ListViewAdapter adapter;
    List<String> listTag, listTagPre, secondList, listCount;
    private ArrayList<ListViewAdapter.ListViewItem> listViewItemList = new ArrayList<ListViewAdapter.ListViewItem>();

    List<Long> favoritesList;
    List<String> stringList;
    String[] tagList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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

        initDataBase();
        initSharedPreferences();
        setContentView(R.layout.activity_favorites);
        initView();
        initListener();

    }

    private void initView() {

        favoritesListView = (ListView) findViewById(R.id.favoriteListView);

        adapter = new ListViewAdapter();
        listTag = new ArrayList<>();
        listTagPre = new ArrayList<>();
        secondList = new ArrayList<>();
        listCount = new ArrayList<>();


        favoritesList = diaryDao.getFavoriteDate();
        /* Specify the size of the list up front to prevent resizing. */
        stringList = new ArrayList<>(favoritesList.size());
        for (Long mylong : favoritesList) {
            stringList.add(String.valueOf(mylong));
        }

        tagList = stringList.toArray(new String[stringList.size()]);

        for (int i = 0; i < tagList.length; i++) {
            if (tagList[i].length() != 0) {
                listTagPre.add(tagList[i]);
                secondList.add(tagList[i]);
            } else {
                listTagPre.add(" 태그 없음...");
                secondList.add(" 태그 없음...");
            }
        }

        favoritesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                long day_tag = Long.parseLong(listTag.get(i));

                Intent cal = new Intent(FavoritesActivity.this, ReadDiaryActivity.class);
                cal.putExtra("checkId", day_tag);
                startActivity(cal);

            }
        });



        Set<String> set = new HashSet<String>(listTagPre);
        for (String str : set) {
            count = String.valueOf(Collections.frequency(listTagPre, str));
            listTag.add(str);
        }


        favoritesListView.setAdapter(adapter);
        for (int i = 0; i < listTag.size(); i++) {
            adapter.addItem("#" + listTag.get(i));

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
    }

    private void initSharedPreferences() {
        sharedPref = getSharedPreferences("CatDiary", Context.MODE_PRIVATE);
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

            // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
            ListViewItem listViewItem = listViewItemList.get(position);

            // 아이템 내 각 위젯에 데이터 반영
            titleTextView.setText(listViewItem.getTitle());

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
        public void addItem(String tag) {
            ListViewItem item = new ListViewItem();
            item.setTitle(tag);
            listViewItemList.add(item);
        }

        public class ListViewItem {
            private String titleStr;
            public void setTitle(String title) {
                titleStr = title;
            }
            public String getTitle() {
                return this.titleStr;
            }
        }

    }
}
