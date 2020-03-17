package com.match.honey.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.match.honey.R;
import com.match.honey.databinding.ActivityQnaReportDetailBinding;
import com.match.honey.utils.StringUtil;

public class QnaReportDetailAct extends AppCompatActivity {
    ActivityQnaReportDetailBinding binding;
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_qna_report_detail, null);

        Intent from = getIntent();

        binding.tvQnaType.setText(from.getStringExtra("type"));

        binding.tvRegdate.setText(from.getStringExtra("regdate"));
        binding.tvQuestion.setText(from.getStringExtra("question"));

        if (!StringUtil.isNull(from.getStringExtra("answer")))
            binding.tvAnswer.setText(from.getStringExtra("answer"));


        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
