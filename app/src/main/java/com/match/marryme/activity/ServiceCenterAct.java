package com.match.marryme.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.match.marryme.R;
import com.match.marryme.databinding.ActivityServicecenterBinding;
import com.match.marryme.utils.StringUtil;

public class ServiceCenterAct extends Activity implements View.OnClickListener {
    ActivityServicecenterBinding binding;
    public static Activity act;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_servicecenter);
        act = this;

        binding.flBack.setOnClickListener(this);
        binding.btnQna.setOnClickListener(this);
        binding.llHome.setOnClickListener(this);

        if(!StringUtil.isNull(getIntent().getStringExtra("from"))) {
            binding.llHome.setVisibility(View.GONE);
            binding.btnNomember.setVisibility(View.VISIBLE);
            binding.btnQna.setVisibility(View.GONE);

            binding.btnNomember.setOnClickListener(this);
        } else {
            binding.llHome.setVisibility(View.GONE);
            binding.btnNomember.setVisibility(View.GONE);
            binding.btnQna.setVisibility(View.VISIBLE);

            binding.btnQna.setOnClickListener(this);
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

            case R.id.btn_qna:
                startActivity(new Intent(this, QnaAct.class));
                break;

            case R.id.btn_nomember:
                startActivity(new Intent(act, QnaNomemberAct.class));
                break;

        }
    }
}
