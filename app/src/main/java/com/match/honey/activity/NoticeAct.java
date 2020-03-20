package com.match.honey.activity;

import android.app.Activity;
import android.content.Context;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.match.honey.R;
import com.match.honey.adapters.list.NoticeAdapter;
import com.match.honey.databinding.ActivityNoticeBinding;
import com.match.honey.listDatas.NoticeData;
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

/**
 * 공지사항 페이지
 * 완료
 */
public class NoticeAct extends Activity implements View.OnClickListener {

    ActivityNoticeBinding binding;

    ArrayList<NoticeData> list;
    ArrayList<NoticeData> search_list = new ArrayList<>();
    NoticeAdapter adapter;
    LinearLayoutManager mManager;
    public boolean isScroll = false;
    public boolean isSearch = false;
    public int page;
    int lastPage = 0;
    public static Activity act;
    private InputMethodManager imm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_notice);
        act = this;

        binding.flBack.setOnClickListener(this);
        binding.llHome.setOnClickListener(this);
        binding.flSearch.setOnClickListener(this);

        mManager = new LinearLayoutManager(this);
        imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);

        binding.rcvNotice.setLayoutManager(mManager);
        adapter = new NoticeAdapter(this, list);
        binding.rcvNotice.setAdapter(adapter);
        binding.rcvNotice.addItemDecoration(new ItemOffsetDecorationCustom(act, getResources().getDimensionPixelSize(R.dimen.dimen_7)));

        binding.rcvNotice.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalCount = mManager.getItemCount();
                int lastComplete = mManager.findLastCompletelyVisibleItemPosition();
                if (!isScroll && !isSearch) {
                    if (totalCount - 1 == lastComplete) {
                        isScroll = true;
                        page++;
                        getNotice(page);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        isSearch = false;
        binding.etSearch.setText(null);

        page = 1;
        list = new ArrayList<>();
        getNotice(page);
    }

    private void getNotice(int page) {
        isScroll = true;
        ReqBasic getNotice = new ReqBasic(this, NetUrls.BOARDLIST) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        Log.e("TEST_HOME", "Notice List Get Info: " + jo);

                        if (jo.getString("result").equalsIgnoreCase(StringUtil.RSUCCESS)) {

                            JSONArray ja = new JSONArray(jo.getString("value"));
                            ArrayList<NoticeData> tmplist = new ArrayList<>();

                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject obj = ja.getJSONObject(i);
                                NoticeData data = new NoticeData();
                                data.setIdx(obj.getString("idx"));
                                data.setTitle(obj.getString("title"));
                                data.setRegdate(obj.getString("created_at"));
                                data.setContents(obj.getString("content"));
                                data.setHit(obj.getString("hit"));
                                Log.i(StringUtil.TAG, "notice obj: " + obj.toString());

                                tmplist.add(data);
                            }

                            list.addAll(tmplist);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setList(list);
                                }
                            });

                        } else if (jo.getString("result").equalsIgnoreCase(StringUtil.RFAIL)) {
//                            Toast.makeText(NoticeAct.this, "불러오기 완료", Toast.LENGTH_SHORT).show();
                        } else {
//                            Toast.makeText(NoticeAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                        }
                        isScroll = false;
                        isSearch = false;
                    } catch (JSONException e) {
                        isScroll = false;
                        isSearch = false;
                        e.printStackTrace();
                        Toast.makeText(NoticeAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    isScroll = false;
                    isSearch = false;
                    Toast.makeText(NoticeAct.this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                }
            }
        };

        getNotice.addParams("btype", "notice");
        getNotice.addParams("pg", String.valueOf(page));
        getNotice.execute(true, false);
    }

    private void doSearch(String keyword) {
        isSearch = true;

        search_list = new ArrayList<>();

        if (StringUtil.isNull(keyword)) {
            page = 1;
            list = new ArrayList<>();
            getNotice(page);
        } else {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getTitle().contains(keyword)) {
                    search_list.add(list.get(i));
                }
            }

            if (search_list.size() == 0) {
                Common.showToast(act, "해당하는 공지사항이 없습니다");
            } else {
                adapter.setList(search_list);
            }
        }
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

            case R.id.fl_search:
                imm.hideSoftInputFromWindow(binding.etSearch.getWindowToken(), 0);
                doSearch(binding.etSearch.getText().toString());
                break;
        }
    }
}
