package com.match.honey.dialog;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.match.honey.R;
import com.match.honey.activity.SplashAct;
import com.match.honey.databinding.DlgPushBinding;
import com.match.honey.utils.StringUtil;

public class DlgPush extends Activity{

    DlgPushBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        // 아래에 원하는 투명도를 넣으면 된다.
        lpWindow.dimAmount = 0.6f;
        lpWindow.height = (int) (deviceHeight * 0.85f);
        getWindow().setAttributes(lpWindow);

        binding = DataBindingUtil.setContentView(this, R.layout.dlg_push);

        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("contents");

        if (!StringUtil.isNull(title)) {
            binding.tvTitle.setText(title);
        }else{
            binding.tvTitle.setText("여보자기");
        }

        if (!StringUtil.isNull(content)) {
            binding.tvContent.setText(content);
        }else{
            binding.tvContent.setText("푸시 수신 오류");
        }

        binding.llAllArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                startActivity(new Intent(DlgPush.this, SplashAct.class));
                finish();
            }
        });
    }
}
