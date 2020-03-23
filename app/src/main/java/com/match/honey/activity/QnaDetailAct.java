package com.match.honey.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.match.honey.R;
import com.match.honey.databinding.ActivityQnaDetailBinding;
import com.match.honey.utils.StringUtil;

public class QnaDetailAct extends AppCompatActivity {
    ActivityQnaDetailBinding binding;
    AppCompatActivity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_qna_detail, null);

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
