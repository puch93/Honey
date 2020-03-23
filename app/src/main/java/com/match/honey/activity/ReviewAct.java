package com.match.honey.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.match.honey.R;
import com.match.honey.adapters.list.ThanksListAdapter;
import com.match.honey.databinding.ActivityThanksBinding;
import com.match.honey.listDatas.ThanksData;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.utils.Common;
import com.match.honey.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReviewAct extends AppCompatActivity implements View.OnClickListener {

    ActivityThanksBinding binding;
    public static AppCompatActivity act;

    ArrayList<ThanksData> list = new ArrayList<>();

    ThanksListAdapter adapter;
    LinearLayoutManager mManager;
    public boolean isScroll = false;
    public int page = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_thanks);
        act = this;

        binding.flBack.setOnClickListener(this);
        binding.llHome.setOnClickListener(this);
        binding.btnShare.setOnClickListener(this);
        binding.btnSendmail.setOnClickListener(this);
        binding.btnGreviewreg.setOnClickListener(this);

        mManager = new LinearLayoutManager(this);

        binding.rcvReviews.setLayoutManager(mManager);
        adapter = new ThanksListAdapter(this, list);
        binding.rcvReviews.setAdapter(adapter);
//        binding.rcvReviews.addItemDecoration(new ItemOffsetDecorationCustom(this, getResources().getDimensionPixelSize(R.dimen.dimen_7)));

        binding.rcvReviews.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalCount = mManager.getItemCount();
                int lastComplete = mManager.findLastCompletelyVisibleItemPosition();
                if (!isScroll) {
                    if (totalCount - 1 == lastComplete) {
                        isScroll = true;
                        page++;
                        reviewList(page);
                    }
                }
            }
        });

//        reviewList(page);
    }

    @Override
    protected void onResume() {
        super.onResume();
        page = 1;
        list.clear();
        reviewList(page);
    }

    private void reviewList(int page) {
        isScroll = true;
        ReqBasic reviewList = new ReqBasic(this, NetUrls.REVIEWLIST) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Review List Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {

                            JSONArray ja = new JSONArray(jo.getString("value"));
                            ArrayList<ThanksData> tmplist = new ArrayList<>();

                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject obj = ja.getJSONObject(i);
                                ThanksData data = new ThanksData();
                                data.setBidx(obj.getString("idx"));
//                                data.setMidx(obj.getString("user_idx"));
                                data.setContents(obj.getString("content"));
                                data.setRegdate(obj.getString("created_at"));
                                data.setHit(obj.getString("hit"));

                                data.setNick(obj.getString("nick"));
                                data.setMarriage(obj.getString("ct"));
                                data.setGender(obj.getString("gender"));
                                data.setProfimg(obj.getString("profimg"));
                                data.setPimg_ck(obj.getString("pimg_ck"));
                                data.setPint_ck(obj.getString("pint_ck"));
                                data.setCharacter(Common.getCharacterDrawable(obj.getString("character"), obj.getString("gender")));
                                data.setAddr1(obj.getString("addr1"));
                                data.setAddr2(obj.getString("addr2"));
                                data.setPint(obj.getString("pint"));

                                tmplist.add(data);
                            }

                            list.addAll(tmplist);
                            adapter.notifyDataSetChanged();

                        } else if (jo.getString("result").equalsIgnoreCase(StringUtil.RFAIL)) {
//                            Toast.makeText(ReviewAct.this, "불러오기 완료", Toast.LENGTH_SHORT).show();
                        } else {
//                            Toast.makeText(ReviewAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                        }
                        isScroll = false;
                    } catch (JSONException e) {
                        isScroll = false;
                        e.printStackTrace();
                        Toast.makeText(ReviewAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    isScroll = false;
                    Toast.makeText(ReviewAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }

            }
        };

        reviewList.addParams("btype", "ureview");
        reviewList.addParams("pg", String.valueOf(page));
        reviewList.execute(true, true);
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

            case R.id.btn_share:
                Intent in = new Intent(Intent.ACTION_SEND);
                in.addCategory(Intent.CATEGORY_DEFAULT);
                in.putExtra(Intent.EXTRA_TEXT,"가장 스마트한 결혼방법[여보자기]!\n언제 어디서나 내가 설정한 정보를 통해 원하는 배우자를 찾아보세요!\n여보자기 다운로드 링크\nhttps://play.google.com/store/apps/details?id=com.match.honey");
                in.setType("text/plain");
                startActivity(Intent.createChooser(in, "공유하기"));

                break;
            case R.id.btn_sendmail:
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("text/html");
                email.setPackage("com.google.android.gm");
                email.putExtra(Intent.EXTRA_SUBJECT,"[여보자기]");
                email.putExtra(Intent.EXTRA_TEXT,"가장 스마트한 결혼방법[여보자기]!\n언제 어디서나 내가 설정한 정보를 통해 원하는 배우자를 찾아보세요!\n여보자기 다운로드 링크\nhttps://play.google.com/store/apps/details?id=com.match.honey");
                startActivity(email);
                break;

            case R.id.btn_greviewreg:
                startActivity(new Intent(act, ReviewRegAct.class));
                break;
        }
    }
}
