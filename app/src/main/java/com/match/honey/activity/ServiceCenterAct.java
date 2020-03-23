package com.match.honey.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.match.honey.R;
import com.match.honey.databinding.ActivityServicecenterBinding;
import com.match.honey.utils.StringUtil;

public class ServiceCenterAct extends AppCompatActivity implements View.OnClickListener {
    ActivityServicecenterBinding binding;
    public static AppCompatActivity act;

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
