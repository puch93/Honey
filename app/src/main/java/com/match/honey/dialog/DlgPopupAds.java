package com.match.honey.dialog;

import android.app.Activity;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.match.honey.R;
import com.match.honey.databinding.DlgPopupAdsBinding;

public class DlgPopupAds extends Activity {

    DlgPopupAdsBinding binding;
    Activity act;

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

        binding = DataBindingUtil.setContentView(this, R.layout.dlg_popup_ads);
        act = this;

        String image = getIntent().getStringExtra("imageUrl");
        final String url = getIntent().getStringExtra("url");


        Glide.with(act).load(image).into(binding.ivContents);

//        binding.ivClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        binding.ivContents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });
    }
}
