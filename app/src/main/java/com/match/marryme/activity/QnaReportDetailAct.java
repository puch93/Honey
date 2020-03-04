package com.match.marryme.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.match.marryme.R;
import com.match.marryme.databinding.ActivityQnaReportDetailBinding;
import com.match.marryme.utils.StringUtil;

public class QnaReportDetailAct extends AppCompatActivity {
    ActivityQnaReportDetailBinding binding;
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_qna_report_detail, null);

        Intent from = getIntent();

        binding.tvQnaType.setText(from.getStringExtra("type"));
        binding.tvQnaType2.setText(from.getStringExtra("type"));
        binding.tvQnaType3.setText(from.getStringExtra("type"));

        if (from.getBooleanExtra("state", false)) {
            binding.tvAnswerState.setText("답변완료");
        } else {
            binding.tvAnswerState.setText("답변대기");
        }

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
