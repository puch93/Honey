package com.match.honey.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.match.honey.R;
import com.match.honey.adapters.list.DlgMultipleListAdapter;
import com.match.honey.listDatas.PopData;

import java.util.ArrayList;

public class MultipleListDlgAct extends Activity implements View.OnClickListener{

    RecyclerView rcv_list;

    DlgMultipleListAdapter adapter;
    ArrayList<PopData> list = new ArrayList<>();

    String subject,select;

    Activity act;

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
        lpWindow.height = (int)(deviceHeight*0.85f);
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.dlg_mutiplelist_popup);

        act = this;

        subject = getIntent().getStringExtra("subject");
        select = getIntent().getStringExtra("select");

        rcv_list = findViewById(R.id.rcv_poplist);
        rcv_list.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        ((TextView)findViewById(R.id.btn_selectinit)).setOnClickListener(this);
        ((TextView)findViewById(R.id.btn_select)).setOnClickListener(this);

        setLayout();

    }

    private void setLayout(){
        adapter = new DlgMultipleListAdapter(this,list);
        if (subject.equalsIgnoreCase("hope_loc")){
            setList(R.array.hope_loc);
        } else if (subject.equalsIgnoreCase("hope_loc2")){
            setList(R.array.hope_loc2);
        } else if (subject.equalsIgnoreCase("fashion")){
            adapter.setLimit(true,2);
            setList(R.array.fashion);
        }else if (subject.equalsIgnoreCase("personal")){
            adapter.setLimit(true,3);
            setList(R.array.personal);
        }else if (subject.equalsIgnoreCase("hobby")){
            adapter.setLimit(true,5);
            setList(R.array.hobby);
        }else if (subject.equalsIgnoreCase("food")){
            adapter.setLimit(true,7);
            setList(R.array.food);
        }else if (subject.equalsIgnoreCase("add_religion")){
            setList(R.array.add_religion);
        }else if (subject.equalsIgnoreCase("add_bloodtype")){
            setList(R.array.add_blood);
        }else if (subject.equalsIgnoreCase("add_edu")){
            setList(R.array.add_edu);
        }

        rcv_list.setLayoutManager(new LinearLayoutManager(this));
        rcv_list.setAdapter(adapter);
    }

    private void setList(int arrId){
        String[] items = select.split("\\|");
        for(String item : getResources().getStringArray(arrId)){
            PopData data = new PopData();
            for(int i = 0; i < items.length; i++) {
                if (items[i].equalsIgnoreCase(item)) {
                    data.setSelected(true);
                }
            }
            data.setItem(item);
            list.add(data);
        }
    }

    private void initListSelectState() {
        for (PopData item : list) {
            item.setSelected(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_selectinit:
                initListSelectState();
                if (list.get(0).getItem().equalsIgnoreCase("전체")){
                    list.get(0).setSelected(true);
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.btn_select:
                String selectItem = "";
                for (PopData item : list){
                    if (item.isSelected()){
                        if (selectItem.equalsIgnoreCase("")) {
                            selectItem = item.getItem();
                        }else{
                            selectItem += "|" + item.getItem();
                        }
                    }
                }

                Intent intent = new Intent();
                intent.putExtra("data",selectItem);
                setResult(RESULT_OK,intent);
                finish();
                break;
        }
    }
}
