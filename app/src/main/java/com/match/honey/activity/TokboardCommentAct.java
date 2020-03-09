package com.match.honey.activity;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.match.honey.R;
import com.match.honey.adapters.list.TokboardDetailAdapter;
import com.match.honey.databinding.ActivityTokboardCommentBinding;
import com.match.honey.listDatas.TokboardDetailData;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.utils.Common;
import com.match.honey.utils.ItemOffsetDecorationCustom;
import com.match.honey.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TokboardCommentAct extends AppCompatActivity implements View.OnClickListener {
    ActivityTokboardCommentBinding binding;
    Activity act;

    ArrayList<TokboardDetailData> list = new ArrayList<>();
    ArrayList<TokboardDetailData> selectList = new ArrayList<>();
    TokboardDetailAdapter adapter;

    String idx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tokboard_comment, null);
        act = this;

        idx = getIntent().getStringExtra("idx");

        binding.flBack.setOnClickListener(this);
        binding.llHome.setOnClickListener(this);

        binding.rcvBoarddetaillist.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TokboardDetailAdapter(this, list);
        binding.rcvBoarddetaillist.setAdapter(adapter);
        binding.rcvBoarddetaillist.addItemDecoration(new ItemOffsetDecorationCustom(this, getResources().getDimensionPixelSize(R.dimen.dimen_7)));

//        tokDetailInfo();

        binding.rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rdo_male:
                    case R.id.rdo_female:
                        searchMem();
                        break;
                    case R.id.rdo_all:
                        if (list.size() > 0) {
                            list.clear();
                        }
                        tokDetailInfo();
                        break;
                }
            }
        });

        binding.rdoAll.setChecked(true);
    }

    private void searchMem() {
        if (selectList.size() > 0) {
            selectList.clear();
        }
        for (TokboardDetailData item : list) {
            if (item.getGender().equalsIgnoreCase("male")) {
                if (binding.rdoMale.isChecked()) {
                    selectList.add(item);
                }
            } else {
                if (binding.rdoFemale.isChecked()) {
                    selectList.add(item);
                }
            }
        }

        adapter = new TokboardDetailAdapter(this, selectList);
        binding.rcvBoarddetaillist.setAdapter(adapter);
    }

    private void tokDetailInfo() {
        ReqBasic tokDetail = new ReqBasic(this, NetUrls.TOKLISTDETAIL) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Tok Detail Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {

                            JSONObject val = new JSONObject(jo.getString("value"));

                            JSONArray ja = new JSONArray(val.getString("comment_list"));
                            ArrayList<TokboardDetailData> tmplist = new ArrayList<>();

                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject obj = ja.getJSONObject(i);
                                TokboardDetailData data = new TokboardDetailData();

                                data.setContents(obj.getString("comment"));
                                data.setIdx(obj.getString("idx"));
                                data.setNickname(obj.getString("nick"));
                                data.setProfimg(obj.getString("pimg"));
                                data.setRegdate(obj.getString("regdate"));
                                data.setGender(obj.getString("gender"));
                                data.setUidx(obj.getString("u_idx"));
                                data.setPimg_ck(obj.getString("pimg_ck"));
                                data.setCharacter(Common.getCharacterDrawable(obj.getString("character"), obj.getString("gender")));

                                tmplist.add(data);
                            }

                            list.addAll(tmplist);
                            adapter = new TokboardDetailAdapter(act, list);
                            binding.rcvBoarddetaillist.setAdapter(adapter);

                        } else {
                            Toast.makeText(act, jo.getString("comment"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };

        tokDetail.addParams("tidx", idx);
        tokDetail.execute(true, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;
            case R.id.ll_home:
                if(TokboardDetailAct.act != null) {
                    TokboardDetailAct.act.finish();
                }

                if (TokboardAct.act != null) {
                    TokboardAct.act.finish();
                }

                ((MainActivity) MainActivity.act).fromHomeBtn();

                finish();
                break;
        }
    }
}
