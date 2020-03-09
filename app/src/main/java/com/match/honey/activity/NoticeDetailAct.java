package com.match.honey.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.match.honey.R;
import com.match.honey.databinding.ActivityNoticeBinding;
import com.match.honey.databinding.ActivityNoticeDetailBinding;
import com.match.honey.listDatas.NoticeData;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.sharedPref.UserPref;
import com.match.honey.utils.Common;
import com.match.honey.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NoticeDetailAct extends AppCompatActivity {
    ActivityNoticeDetailBinding binding;
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notice_detail, null);
        act = this;

        Intent from = getIntent();

        setPlusCount(from.getStringExtra("idx"));

        binding.tvTitle.setText(from.getStringExtra("title"));
        binding.tvContents.setText(from.getStringExtra("contents"));
        binding.tvRegdate.setText(from.getStringExtra("regdate"));
        binding.tvViewcount.setText(from.getStringExtra("viewcount"));

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.llHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NoticeAct.act != null) {
                    NoticeAct.act.finish();
                }

                ((MainActivity) MainActivity.act).fromHomeBtn();

                finish();
            }
        });
    }

    private void setPlusCount(String idx) {
        ReqBasic getNotice = new ReqBasic(this, NetUrls.BOARDLISTCOUNT) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Notice Plus Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {

                        } else {
//                            Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
//                        Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
//                    Toast.makeText(act, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };

        getNotice.addParams("idx", idx);
        getNotice.execute(true, true);
    }
}
