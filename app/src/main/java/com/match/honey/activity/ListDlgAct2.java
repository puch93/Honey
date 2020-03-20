package com.match.honey.activity;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.match.honey.R;
import com.match.honey.adapters.RecyclerViewItemClick;
import com.match.honey.adapters.list.DlgListAdapter2;
import com.match.honey.listDatas.PopData2;

import java.util.ArrayList;
import java.util.Calendar;

public class ListDlgAct2 extends Activity {

    RecyclerView rcv_list;

    DlgListAdapter2 adapter;
    ArrayList<PopData2> list = new ArrayList<>();

    String subject, select;
    int cyear;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        // 아래에 원하는 투명도를 넣으면 된다.
        lpWindow.dimAmount = 0.6f;
        lpWindow.height = (int) (deviceHeight * 0.85f);
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dlg_list_popup);

        subject = getIntent().getStringExtra("subject");
        select = getIntent().getStringExtra("select");

        rcv_list = findViewById(R.id.rcv_poplist);
        setLayout();
    }


    private void setLayout() {
        if (subject.equalsIgnoreCase("age")) {
            setList();
        }

        adapter = new DlgListAdapter2(this, list);
        rcv_list.setLayoutManager(new LinearLayoutManager(this));
        rcv_list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rcv_list.setAdapter(adapter);
    }

    private void setList() {
        Calendar c = Calendar.getInstance();
        cyear = c.get(Calendar.YEAR) + 1;

        int current = 20;
        for (String item : getResources().getStringArray(R.array.age)) {
            PopData2 data = new PopData2(item, cyear - current, false);
            if (current == 20) {
                current += 6;
            } else {
                current += 5;
            }
            list.add(data);
        }

        String[] year = select.split("\\|");
        if (year.length > 0) {
            for (int i = 0; i < year.length; i++) {

                int currnet = 20;
                for (int j = 0; j < list.size(); j++) {
                    if(cyear == list.get(j).getItem_year() + current) {
                        list.get(j).setSelected(true);
                    }

                    if (current == 20) {
                        current += 6;
                    } else {
                        current += 5;
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((DlgListAdapter2) adapter).onItemClick(new RecyclerViewItemClick() {
            @Override
            public void onItemClick(int position, View v) {

            }

            @Override
            public void onItemClick2(ArrayList<PopData2> list2) {
                list = list2;
            }
        });
    }
}
