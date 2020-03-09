package com.match.honey.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.match.honey.R;
import com.match.honey.adapters.RecyclerViewItemClick;
import com.match.honey.adapters.list.LastnameListAdapter;
import com.match.honey.databinding.ActivityLastnamesearchBinding;
import com.match.honey.listDatas.LastnameData;
import com.match.honey.utils.CustomDividerItemDecoration;
import com.match.honey.utils.DefaultValue;
import com.match.honey.utils.StringUtil;

import java.util.ArrayList;

public class LastnameSearchAct extends Activity implements View.OnClickListener{

    ActivityLastnamesearchBinding binding;

    LastnameListAdapter adapter;
    ArrayList<LastnameData> totallastname = new ArrayList<>();
    ArrayList<LastnameData> searchlist = new ArrayList<>();
    public ArrayList<LastnameData> selectlastname = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_lastnamesearch);

        setListData();

        binding.rcvLastname.setLayoutManager(new GridLayoutManager(this,2));
        adapter = new LastnameListAdapter(this,totallastname);
        binding.rcvLastname.setAdapter(adapter);

        setClick();

        if (!StringUtil.isNull(getIntent().getStringExtra("data"))) {
            String[] lastnames = getIntent().getStringExtra("data").split("\\|");

            for (LastnameData data : totallastname){
                for (int i = 0; i < lastnames.length; i++){
                    if(data.getLastnameText().equalsIgnoreCase(lastnames[i])){
                        data.setCheckstate(true);
                        addSelectList(data);
                    }
                }
            }
        }

    }


    private void setSelectLayout(){
        for (int i = 0; i < selectlastname.size(); i++){
            switch (i){
                case 0:
                    binding.llSel01.setVisibility(View.VISIBLE);
                    binding.tvSel01.setText(selectlastname.get(0).getLastnameText());
                    break;
                case 1:
                    binding.llSel02.setVisibility(View.VISIBLE);
                    binding.tvSel02.setText(selectlastname.get(1).getLastnameText());
                    break;
                case 2:
                    binding.llSel03.setVisibility(View.VISIBLE);
                    binding.tvSel03.setText(selectlastname.get(2).getLastnameText());
                    break;
                case 3:
                    binding.llSel04.setVisibility(View.VISIBLE);
                    binding.tvSel04.setText(selectlastname.get(3).getLastnameText());
                    break;
                case 4:
                    binding.llSel05.setVisibility(View.VISIBLE);
                    binding.tvSel05.setText(selectlastname.get(4).getLastnameText());
                    break;
            }
        }
    }

    public void addSelectList(LastnameData item){
        selectlastname.add(item);
        if (selectlastname.size() > 0) {
            setSelectLayout();
        }
    }

    public void removeSelectList(LastnameData item){
        selectlastname.remove(item);

        binding.llSel01.setVisibility(View.INVISIBLE);
        binding.llSel02.setVisibility(View.INVISIBLE);
        binding.llSel03.setVisibility(View.INVISIBLE);
        binding.llSel04.setVisibility(View.INVISIBLE);
        binding.llSel05.setVisibility(View.INVISIBLE);

        if (selectlastname.size() > 0) {
            setSelectLayout();
        }
    }

    private void setListData(){
        for (String item : getResources().getStringArray(R.array.lastname01)){
            LastnameData data = new LastnameData();
            data.setLastnameText(item);
            data.setInitial("ㄱ");
            totallastname.add(data);
        }

        for (String item : getResources().getStringArray(R.array.lastname02)){
            LastnameData data = new LastnameData();
            data.setLastnameText(item);
            data.setInitial("ㄴ");
            totallastname.add(data);
        }

        for (String item : getResources().getStringArray(R.array.lastname03)){
            LastnameData data = new LastnameData();
            data.setLastnameText(item);
            data.setInitial("ㄷ");
            totallastname.add(data);
        }

        for (String item : getResources().getStringArray(R.array.lastname04)){
            LastnameData data = new LastnameData();
            data.setLastnameText(item);
            data.setInitial("ㄹ");
            totallastname.add(data);
        }

        for (String item : getResources().getStringArray(R.array.lastname05)){
            LastnameData data = new LastnameData();
            data.setLastnameText(item);
            data.setInitial("ㅁ");
            totallastname.add(data);
        }

        for (String item : getResources().getStringArray(R.array.lastname06)){
            LastnameData data = new LastnameData();
            data.setLastnameText(item);
            data.setInitial("ㅂ");
            totallastname.add(data);
        }

        for (String item : getResources().getStringArray(R.array.lastname07)){
            LastnameData data = new LastnameData();
            data.setLastnameText(item);
            data.setInitial("ㅅ");
            totallastname.add(data);
        }

        for (String item : getResources().getStringArray(R.array.lastname08)){
            LastnameData data = new LastnameData();
            data.setLastnameText(item);
            data.setInitial("ㅇ");
            totallastname.add(data);
        }

        for (String item : getResources().getStringArray(R.array.lastname09)){
            LastnameData data = new LastnameData();
            data.setLastnameText(item);
            data.setInitial("ㅈ");
            totallastname.add(data);
        }

        for (String item : getResources().getStringArray(R.array.lastname10)){
            LastnameData data = new LastnameData();
            data.setLastnameText(item);
            data.setInitial("ㅊ");
            totallastname.add(data);
        }

//        for (String item : getResources().getStringArray(R.array.lastname11)){
//            LastnameData data = new LastnameData();
//            data.setLastnameText(item);
//            data.setInitial("ㅋ");
//            totallastname.add(data);
//        }

        for (String item : getResources().getStringArray(R.array.lastname12)){
            LastnameData data = new LastnameData();
            data.setLastnameText(item);
            data.setInitial("ㅌ");
            totallastname.add(data);
        }

        for (String item : getResources().getStringArray(R.array.lastname13)){
            LastnameData data = new LastnameData();
            data.setLastnameText(item);
            data.setInitial("ㅍ");
            totallastname.add(data);
        }

        for (String item : getResources().getStringArray(R.array.lastname14)){
            LastnameData data = new LastnameData();
            data.setLastnameText(item);
            data.setInitial("ㅎ");
            totallastname.add(data);
        }

    }

    private void setClick(){
        binding.llBtnBack.setOnClickListener(this);
        binding.btnHome.setOnClickListener(this);
        binding.btnJa01.setOnClickListener(this);
        binding.btnJa02.setOnClickListener(this);
        binding.btnJa03.setOnClickListener(this);
        binding.btnJa04.setOnClickListener(this);
        binding.btnJa05.setOnClickListener(this);
        binding.btnJa06.setOnClickListener(this);
        binding.btnJa07.setOnClickListener(this);
        binding.btnJa08.setOnClickListener(this);
        binding.btnJa09.setOnClickListener(this);
        binding.btnJa10.setOnClickListener(this);
        binding.btnJa11.setOnClickListener(this);
        binding.btnJa12.setOnClickListener(this);
        binding.btnJa13.setOnClickListener(this);
        binding.btnJa14.setOnClickListener(this);
        binding.btnTotalname.setOnClickListener(this);
        binding.btnInit.setOnClickListener(this);
        binding.btnSearch.setOnClickListener(this);

        binding.ivClose01.setOnClickListener(this);
        binding.ivClose02.setOnClickListener(this);
        binding.ivClose03.setOnClickListener(this);
        binding.ivClose04.setOnClickListener(this);
        binding.ivClose05.setOnClickListener(this);
    }

    private void searchlistClose(int pos){
        totallastname.get(totallastname.indexOf(selectlastname.get(pos))).setCheckstate(false);
        selectlastname.remove(pos);
        adapter.notifyDataSetChanged();

        binding.llSel01.setVisibility(View.INVISIBLE);
        binding.llSel02.setVisibility(View.INVISIBLE);
        binding.llSel03.setVisibility(View.INVISIBLE);
        binding.llSel04.setVisibility(View.INVISIBLE);
        binding.llSel05.setVisibility(View.INVISIBLE);

        if (selectlastname.size() > 0) {
            setSelectLayout();
        }
    }

    private void setSearchlist(String initial){
        if (searchlist.size() > 0) {
            searchlist.clear();
        }
        for (LastnameData item : totallastname){
            if (item.getInitial().equalsIgnoreCase(initial)){
                searchlist.add(item);
            }
        }

        adapter = new LastnameListAdapter(this,searchlist);
        binding.rcvLastname.setAdapter(adapter);
    }

    private void initInitialBtn(){
        binding.btnJa01.setSelected(false);
        binding.btnJa02.setSelected(false);
        binding.btnJa03.setSelected(false);
        binding.btnJa04.setSelected(false);
        binding.btnJa05.setSelected(false);
        binding.btnJa06.setSelected(false);
        binding.btnJa07.setSelected(false);
        binding.btnJa08.setSelected(false);
        binding.btnJa09.setSelected(false);
        binding.btnJa10.setSelected(false);
        binding.btnJa11.setSelected(false);
        binding.btnJa12.setSelected(false);
        binding.btnJa13.setSelected(false);
        binding.btnJa14.setSelected(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_btn_back:
                finish();
                break;
            case R.id.btn_home:
                // 메인 이동
                startActivity(new Intent(this,MainActivity.class));
                finishAffinity();
                break;
            case R.id.btn_ja01:
                initInitialBtn();
                binding.btnJa01.setSelected(true);
                setSearchlist("ㄱ");
                break;
            case R.id.btn_ja02:
                initInitialBtn();
                binding.btnJa02.setSelected(true);
                setSearchlist("ㄴ");
                break;
            case R.id.btn_ja03:
                initInitialBtn();
                binding.btnJa03.setSelected(true);
                setSearchlist("ㄷ");
                break;
            case R.id.btn_ja04:
                initInitialBtn();
                binding.btnJa04.setSelected(true);
                setSearchlist("ㄹ");
                break;
            case R.id.btn_ja05:
                initInitialBtn();
                binding.btnJa05.setSelected(true);
                setSearchlist("ㅁ");
                break;
            case R.id.btn_ja06:
                initInitialBtn();
                binding.btnJa06.setSelected(true);
                setSearchlist("ㅂ");
                break;
            case R.id.btn_ja07:
                initInitialBtn();
                binding.btnJa07.setSelected(true);
                setSearchlist("ㅅ");
                break;
            case R.id.btn_ja08:
                initInitialBtn();
                binding.btnJa08.setSelected(true);
                setSearchlist("ㅇ");
                break;
            case R.id.btn_ja09:
                initInitialBtn();
                binding.btnJa09.setSelected(true);
                setSearchlist("ㅈ");
                break;
            case R.id.btn_ja10:
                initInitialBtn();
                binding.btnJa10.setSelected(true);
                setSearchlist("ㅊ");
                break;
            case R.id.btn_ja11:
                initInitialBtn();
                binding.btnJa11.setSelected(true);
//                setSearchlist("ㅋ");

                break;
            case R.id.btn_ja12:
                initInitialBtn();
                binding.btnJa12.setSelected(true);
                setSearchlist("ㅌ");
                break;
            case R.id.btn_ja13:
                initInitialBtn();
                binding.btnJa13.setSelected(true);
                setSearchlist("ㅍ");
                break;
            case R.id.btn_ja14:
                initInitialBtn();
                binding.btnJa14.setSelected(true);
                setSearchlist("ㅎ");
                break;
            case R.id.btn_totalname:
                initInitialBtn();
                adapter = new LastnameListAdapter(this,totallastname);
                binding.rcvLastname.setAdapter(adapter);
                break;
            case R.id.btn_init:

                for (LastnameData item : selectlastname) {
                    totallastname.get(totallastname.indexOf(item)).setCheckstate(false);
                }
                adapter.notifyDataSetChanged();

                selectlastname.clear();

                binding.llSel01.setVisibility(View.INVISIBLE);
                binding.llSel02.setVisibility(View.INVISIBLE);
                binding.llSel03.setVisibility(View.INVISIBLE);
                binding.llSel04.setVisibility(View.INVISIBLE);
                binding.llSel05.setVisibility(View.INVISIBLE);

                if (selectlastname.size() > 0) {
                    setSelectLayout();
                }
                break;
            case R.id.btn_search:
                String data = "";
                for (LastnameData item : selectlastname){
                    if (data.equalsIgnoreCase("")){
                        data = item.getLastnameText();
                    }else {
                        data += "|" + item.getLastnameText();
                    }
                }

                Log.i(StringUtil.TAG,"res: "+data);
                Intent result = new Intent();
                result.putExtra("data",data);
                result.putExtra("arr",selectlastname);
                setResult(RESULT_OK,result);
                finish();
                break;
            case R.id.iv_close01:
                searchlistClose(0);
                break;
            case R.id.iv_close02:
                searchlistClose(1);
                break;
            case R.id.iv_close03:
                searchlistClose(2);
                break;
            case R.id.iv_close04:
                searchlistClose(3);
                break;
            case R.id.iv_close05:
                searchlistClose(4);
                break;
        }
    }
}
