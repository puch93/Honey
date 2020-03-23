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
import com.match.honey.adapters.list.MyQnaListAdapter;
import com.match.honey.databinding.ActivityQnaReportBinding;
import com.match.honey.listDatas.MyQnaData;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.sharedPref.UserPref;
import com.match.honey.utils.ItemOffsetDecorationCustom;
import com.match.honey.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class QnaReportAct extends AppCompatActivity implements View.OnClickListener {
    ActivityQnaReportBinding binding;

    ArrayList<MyQnaData> list = new ArrayList<>();
    MyQnaListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_qna_report);

        binding.flBack.setOnClickListener(this);
        binding.llHome.setOnClickListener(this);

        binding.rcvMyquestion.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyQnaListAdapter(this, list, "report");
        binding.rcvMyquestion.setAdapter(adapter);
        binding.rcvMyquestion.addItemDecoration(new ItemOffsetDecorationCustom(this, getResources().getDimensionPixelSize(R.dimen.dimen_7)));

        myQnaList();
    }

    private void myQnaList() {
        ReqBasic myQna = new ReqBasic(this, NetUrls.QNALISTREPORT) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Qna List Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {

                            JSONArray ja = new JSONArray(jo.getString("value"));
                            ArrayList<MyQnaData> tmplist = new ArrayList<>();

                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject obj = ja.getJSONObject(i);
                                MyQnaData data = new MyQnaData();

                                String answer = obj.getString("a_content");
                                answer = answer.replaceAll("<br\\/>", "\n");
                                data.setAnswer(answer);
                                data.setIdx(obj.getString("idx"));

                                String qtype = "";
                                if (obj.getString("r_type").equalsIgnoreCase("chat")) {
                                    qtype = "채팅관련";
                                } else if (obj.getString("r_type").equalsIgnoreCase("review")) {
                                    qtype = "댓글관련";
                                } else if (obj.getString("r_type").equalsIgnoreCase("profile")) {
                                    qtype = "프로필관련";
                                } else if (obj.getString("r_type").equalsIgnoreCase("etc")) {
                                    qtype = "기타";
                                }
                                data.setQnatype(qtype);

                                String question = obj.getString("r_content");
                                question = question.replaceAll("<br\\/>", "\n");
                                data.setQtext(question);

                                if (obj.getString("r_status").equalsIgnoreCase("answer")) {
                                    data.setReplystate(true);
                                } else {
                                    data.setReplystate(false);
                                }

                                data.setRegdate(obj.getString("r_regdate"));
                                data.setAttach(obj.getString("r_referfile"));

                                obj.getString("a_content");
                                obj.getString("a_regdate");     // 답변시간
                                obj.getString("idx");       // 문의 idx
                                obj.getString("r_status");    // question | answer
                                obj.getString("r_content");
                                obj.getString("r_referfile");
                                obj.getString("r_regdate");
                                obj.getString("r_type");
                                obj.getString("u_cellnum");
                                obj.getString("u_idx");     // 유저

                                tmplist.add(data);

                                Log.e("TEST_HOME", "onData: " + data);
                            }
                            list.addAll(tmplist);
                            adapter.notifyDataSetChanged();


                            Log.e("TEST_HOME", "success");
                        } else {
                            Log.e("TEST_HOME", "non-success");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(QnaReportAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(QnaReportAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }

            }
        };

        myQna.addParams("uidx", UserPref.getUidx(this));

        myQna.execute(true, true);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;
            case R.id.ll_home:
                if(ServiceCenterAct.act != null) {
                    ServiceCenterAct.act.finish();
                }

                ((MainActivity) MainActivity.act).fromHomeBtn();

                finish();
                break;
        }
    }
}
