package com.match.honey.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.match.honey.R;
import com.match.honey.adapters.list.TokboardAdapter;
import com.match.honey.databinding.ActivityToktokboardBinding;
import com.match.honey.listDatas.TokboardData;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TokboardAct extends AppCompatActivity implements View.OnClickListener {

    ActivityToktokboardBinding binding;

    ArrayList<TokboardData> list = new ArrayList<>();
    TokboardAdapter adapter;

    public static AppCompatActivity act;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_toktokboard);
        act = this;

        binding.flBack.setOnClickListener(this);
        binding.llHome.setOnClickListener(this);

        binding.rcvTokboard.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TokboardAdapter(this, list);
        binding.rcvTokboard.setAdapter(adapter);

        tokList(1);
    }

    private void tokList(int page) {
        ReqBasic tokList = new ReqBasic(this, NetUrls.TOKLIST) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Tok List Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {
                            JSONArray ja = new JSONArray(jo.getString("value"));
                            ArrayList<TokboardData> tmplist = new ArrayList<>();

                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject obj = ja.getJSONObject(i);
                                TokboardData data = new TokboardData();

                                data.setBoardimg(obj.getString("title_thumb"));
                                data.setDetailtitle(obj.getString("title"));
                                data.setSubimg(obj.getString("content"));
                                data.setSubject(obj.getString("comment_default"));
                                data.setHit(obj.getString("hit"));
                                data.setIdx(obj.getString("idx"));
                                data.setIdx(obj.getString("idx"));
                                data.setComment_count(obj.getString("comment_count"));

                                tmplist.add(data);
                            }

                            list.addAll(tmplist);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(TokboardAct.this, jo.getString("comment"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(TokboardAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TokboardAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };

        tokList.addParams("pg", String.valueOf(page));
        tokList.execute(true, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;
            case R.id.ll_home:
                ((MainActivity) MainActivity.act).fromHomeBtn();
                finish();
                break;

        }
    }
}
