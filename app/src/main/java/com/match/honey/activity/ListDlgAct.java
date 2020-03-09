package com.match.honey.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.match.honey.R;
import com.match.honey.adapters.RecyclerViewItemClick;
import com.match.honey.adapters.list.DlgListAdapter;
import com.match.honey.listDatas.PopData;
import com.match.honey.listDatas.PopData2;
import com.match.honey.utils.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;

public class ListDlgAct extends Activity {

    RecyclerView rcv_list;

    DlgListAdapter adapter;
    ArrayList<PopData> list = new ArrayList<>();

    String subject, select, mloc;

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
        mloc = getIntent().getStringExtra("mloc");

        rcv_list = findViewById(R.id.rcv_poplist);


        setLayout();

    }

    private void setLayout() {

        if (subject.equalsIgnoreCase("portal")) {
            setList(R.array.portals);
        } else if (subject.equalsIgnoreCase("birth")) {
            Calendar c = Calendar.getInstance();
            int lastYear = c.get(Calendar.YEAR) - 19;

            if (select.equalsIgnoreCase("태어난 년도")) {
                list.add(new PopData(select, true));
            } else {
                list.add(new PopData("태어난 년도"));
            }

            for (int i = lastYear; i > (lastYear - 100); i--) {
                if (select.equalsIgnoreCase(String.valueOf(i))) {
                    list.add(new PopData(String.valueOf(i), true));
                } else {
                    list.add(new PopData(String.valueOf(i)));
                }
            }

        } else if (subject.equalsIgnoreCase("main_loc")) {
            setList(R.array.main_loc);
        } else if (subject.equalsIgnoreCase("middle_loc")) {

            if (select.equalsIgnoreCase("상세지역")) {
                list.add(new PopData(select, true));
            } else {
                switch (mloc) {
                    case "서울":
                        setList(R.array.seoul);
                        break;
                    case "경기":
                        setList(R.array.gyeongi);
                        break;
                    case "인천":
                        setList(R.array.incheon);
                        break;
                    case "대전":
                        setList(R.array.daejeon);
                        break;
                    case "대구":
                        setList(R.array.daegu);
                        break;
                    case "부산":
                        setList(R.array.busan);
                        break;
                    case "광주":
                        setList(R.array.gwangju);
                        break;
                    case "세종":
                        setList(R.array.sejong);
                        break;
                    case "울산":
                        setList(R.array.ulsan);
                        break;
                    case "충북":
                        setList(R.array.chungcheongbuk);
                        break;
                    case "충남":
                        setList(R.array.chungcheongnam);
                        break;
                    case "전북":
                        setList(R.array.jeollabuk);
                        break;
                    case "전남":
                        setList(R.array.jeollanam);
                        break;
                    case "경북":
                        setList(R.array.gyeongsangbuk);
                        break;
                    case "경남":
                        setList(R.array.gyeongsangnam);
                        break;
                    case "강원":
                        setList(R.array.gangwon);
                        break;
                    case "제주":
                        setList(R.array.jeju);
                        break;
                    case "북한":
                        setList(R.array.northk);
                        break;
                }
            }
        } else if (subject.equalsIgnoreCase("middle_loc_search")) {
            if (select.equalsIgnoreCase("전체")) {
                list.add(new PopData("전체", true));
            } else {
                list.add(new PopData("전체", false));
            }

            switch (mloc) {
                case "서울":
                    setList(R.array.seoul);
                    break;
                case "경기":
                    setList(R.array.gyeongi);
                    break;
                case "인천":
                    setList(R.array.incheon);
                    break;
                case "대전":
                    setList(R.array.daejeon);
                    break;
                case "대구":
                    setList(R.array.daegu);
                    break;
                case "부산":
                    setList(R.array.busan);
                    break;
                case "광주":
                    setList(R.array.gwangju);
                    break;
                case "세종":
                    setList(R.array.sejong);
                    break;
                case "울산":
                    setList(R.array.ulsan);
                    break;
                case "충북":
                    setList(R.array.chungcheongbuk);
                    break;
                case "충남":
                    setList(R.array.chungcheongnam);
                    break;
                case "전북":
                    setList(R.array.jeollabuk);
                    break;
                case "전남":
                    setList(R.array.jeollanam);
                    break;
                case "경북":
                    setList(R.array.gyeongsangbuk);
                    break;
                case "경남":
                    setList(R.array.gyeongsangnam);
                    break;
                case "강원":
                    setList(R.array.gangwon);
                    break;
                case "제주":
                    setList(R.array.jeju);
                    break;
                case "북한":
                    setList(R.array.northk);
                    break;
            }
        } else if (subject.equalsIgnoreCase("hope_loc")) {

        } else if (subject.equalsIgnoreCase("add_salary")) {
            setList(R.array.add_salary);
        } else if (subject.equalsIgnoreCase("add_property")) {
            setList(R.array.add_property);
        } else if (subject.equalsIgnoreCase("add_smoke")) {
            setList(R.array.add_smoke);
        } else if (subject.equalsIgnoreCase("add_drinking")) {
            setList(R.array.add_drinking);
        } else if (subject.equalsIgnoreCase("add_children")) {
            setList(R.array.add_child);
        } else if (subject.equalsIgnoreCase("add_marriage")) {
            setList(R.array.add_marriage);
        } else if (subject.equalsIgnoreCase("suggest")) {
            setList(R.array.suggest);
        } else if (subject.equalsIgnoreCase("question")) {
            setList(R.array.question);
        } else if (subject.equalsIgnoreCase("question_report")) {
            setList(R.array.question_report);
        } else if (subject.equalsIgnoreCase("bmonth")) {
            setList(R.array.bmonth);
        } else if (subject.equalsIgnoreCase("bhood1")) {
//            setList(R.array.brotherhood1);
            boolean isMen = getIntent().getBooleanExtra("isMen", true);

            if (isMen) {
                for (String item : getResources().getStringArray(R.array.brotherhood1)) {
                    if (!item.equalsIgnoreCase("0남")) {
                        PopData data = new PopData();
                        if (select.equalsIgnoreCase(item)) {
                            data.setSelected(true);
                        }
                        data.setItem(item);
                        list.add(data);
                    }
                }
            } else {
                for (String item : getResources().getStringArray(R.array.brotherhood1)) {
                    PopData data = new PopData();
                    if (select.equalsIgnoreCase(item)) {
                        data.setSelected(true);
                    }
                    data.setItem(item);
                    list.add(data);
                }
            }
        } else if (subject.equalsIgnoreCase("bhood2")) {
//            setList(R.array.brotherhood2);

            boolean isMen = getIntent().getBooleanExtra("isMen", true);

            if (isMen) {
                for (String item : getResources().getStringArray(R.array.brotherhood2)) {
                    PopData data = new PopData();
                    if (select.equalsIgnoreCase(item)) {
                        data.setSelected(true);
                    }
                    data.setItem(item);
                    list.add(data);
                }
            } else {
                for (String item : getResources().getStringArray(R.array.brotherhood2)) {
                    if (!item.equalsIgnoreCase("0녀")) {
                        PopData data = new PopData();
                        if (select.equalsIgnoreCase(item)) {
                            data.setSelected(true);
                        }
                        data.setItem(item);
                        list.add(data);
                    }
                }
            }
        } else if (subject.equalsIgnoreCase("bhood3")) {
//            setList(R.array.brotherhood3);
            int total = getIntent().getIntExtra("total", 18);

            for (int i = 1; i < total + 1; i++) {
                String item = i + "째";

                PopData data = new PopData();
                if (select.equalsIgnoreCase(item)) {
                    data.setSelected(true);
                }
                data.setItem(item);
                list.add(data);
            }
        } else if (subject.equalsIgnoreCase("mainjob")) {
            setList(R.array.mainjob);
        } else if (subject.equalsIgnoreCase("annual")) {
            setList(R.array.annual);
        } else if (subject.equalsIgnoreCase("annual2")) {
            setList(R.array.annual2);
        } else if (subject.equalsIgnoreCase("edu")) {
            setList(R.array.edu);
        } else if (subject.equalsIgnoreCase("edu2")) {
            setList(R.array.edu2);
        } else if (subject.equalsIgnoreCase("style")) {
            setList(R.array.style);
        } else if (subject.equalsIgnoreCase("height")) {
            setList(R.array.height);
        } else if (subject.equalsIgnoreCase("weight")) {
            setList(R.array.weight);
        } else if (subject.equalsIgnoreCase("smoke")) {
            setList(R.array.smoke);
        } else if (subject.equalsIgnoreCase("smokedetail")) {
            switch (mloc) {
                case "금연":
                    setList(R.array.smoked1);
                    break;
                case "흡연":
                    setList(R.array.smoked2);
                    break;
            }
        } else if (subject.equalsIgnoreCase("drink")) {
            setList(R.array.drink);
        } else if (subject.equalsIgnoreCase("drinkdetail")) {
            if (mloc.equalsIgnoreCase("음주안함")) {
                list.add(new PopData(select, true));
            } else {
                setList(R.array.drinkdetail);
            }
        } else if (subject.equalsIgnoreCase("health")) {
            setList(R.array.health);
        } else if (subject.equalsIgnoreCase("healthdetail")) {
            if (mloc.equalsIgnoreCase("숨쉬기운동")) {
                list.add(new PopData(select, true));
            } else {
                setList(R.array.healthdetail);
            }
        } else if (subject.equalsIgnoreCase("blood")) {
            setList(R.array.blood);
        } else if (subject.equalsIgnoreCase("blood2")) {
            setList(R.array.blood2);
        } else if (subject.equalsIgnoreCase("religion")) {
            setList(R.array.religion);
        } else if (subject.equalsIgnoreCase("religion2")) {
            setList(R.array.religion2);
        } else if (subject.equalsIgnoreCase("carinfo")) {
            setList(R.array.carinfo);
        } else if (subject.equalsIgnoreCase("hopereligion")) {
            setList(R.array.religion);
        } else if (subject.equalsIgnoreCase("boy")) {
            setList(R.array.children);
        } else if (subject.equalsIgnoreCase("girl")) {
            setList(R.array.children);
        } else if (subject.equalsIgnoreCase("property")) {
            setList(R.array.property);
        } else if (subject.equalsIgnoreCase("property2")) {
            setList(R.array.property2);
        } else if (subject.equalsIgnoreCase("abroad")) {
            setList(R.array.abroad);
        } else if (subject.equalsIgnoreCase("joinref")) {
            setList(R.array.joinref);
        } else if (subject.equalsIgnoreCase("selfscore")) {
            for (int i = 40; i <= 100; i++) {
                if (select.equalsIgnoreCase(String.valueOf(i))) {
                    list.add(new PopData(String.valueOf(i), true));
                } else {
                    list.add(new PopData(String.valueOf(i)));
                }
            }
        } else if (subject.equalsIgnoreCase("age")) {
            setList(R.array.age);
        } else if (subject.equalsIgnoreCase("gender")) {
            setList(R.array.gender);
        }

        adapter = new DlgListAdapter(this, list);
        rcv_list.setLayoutManager(new LinearLayoutManager(this));
        rcv_list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rcv_list.setAdapter(adapter);

    }

    private void setList(int arrId) {
        for (String item : getResources().getStringArray(arrId)) {
            PopData data = new PopData();
            if (select.equalsIgnoreCase(item)) {
                data.setSelected(true);
            }
            data.setItem(item);
            list.add(data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((DlgListAdapter) adapter).onItemClick(new RecyclerViewItemClick() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(StringUtil.TAG, "select item: " + list.get(position));
                Intent intent = new Intent();
                intent.putExtra("data", list.get(position).getItem());
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onItemClick2(ArrayList<PopData2> list2) {

            }
        });
    }
}
