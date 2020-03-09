package com.match.honey.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

import com.match.honey.R;
import com.match.honey.adapters.TermsPageAdapter;
import com.match.honey.databinding.ActivityTermBinding;
import com.match.honey.network.ReqBasic;
import com.match.honey.network.netUtil.HttpResult;
import com.match.honey.network.netUtil.NetUrls;
import com.match.honey.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class TermsActivity extends FragmentActivity implements View.OnClickListener {

    ActivityTermBinding binding;
    Activity act;

    private static final int TERM_TYPE01 = 0;
    private static final int TERM_TYPE02 = 1;
    private static final int TERM_TYPE03 = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_term);
        act = this;

        binding.llTerm01.setOnClickListener(this);
        binding.llTerm02.setOnClickListener(this);
        binding.llTerm03.setOnClickListener(this);
        binding.flBack.setOnClickListener(this);

        binding.llTerm01.performClick();
    }

    private void getTerms(final int type) {
        final ReqBasic getTerms = new ReqBasic(act, NetUrls.TERMS) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                Log.i(StringUtil.TAG, "getTerms:  " + resultData.getResult() + "\ncode: " + resultCode);

                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        String term = null;
                        if (type == TERM_TYPE01) {
                            if (jo.has("app_term_condition")) {
                                term = jo.getString("app_term_condition");
                                Log.e(StringUtil.TAG, "term01: " + term);
                            }
                        } else if( type == TERM_TYPE02) {
                            if (jo.has("app_customer_notice")) {
                                term = jo.getString("app_customer_notice");
                                Log.e(StringUtil.TAG, "term02: " + term);
                            }
                        } else {
                            if (jo.has("app_gps_service")) {
                                term = jo.getString("app_gps_service");
                                Log.e(StringUtil.TAG, "term03: " + term);
                            }
                        }

                        final String finalTerm = term;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ScrollView scrollView = (ScrollView) findViewById(R.id.sv_term);

                                scrollView.scrollTo(0, 0);
                                scrollView.pageScroll(View.FOCUS_UP);
                                scrollView.smoothScrollTo(0,0);

                                binding.tvTerm.setText(finalTerm);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        };

        getTerms.execute(true, true);
    }

    private void initTab() {
        binding.llTerm01.setSelected(false);
        binding.llTerm02.setSelected(false);
        binding.llTerm03.setSelected(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;

            case R.id.ll_term01:
                initTab();
                binding.llTerm01.setSelected(true);
                getTerms(TERM_TYPE01);
                break;

            case R.id.ll_term02:
                initTab();
                binding.llTerm02.setSelected(true);
                getTerms(TERM_TYPE02);
                break;

            case R.id.ll_term03:
                initTab();
                binding.llTerm03.setSelected(true);
                getTerms(TERM_TYPE03);
                break;
        }
    }
}
