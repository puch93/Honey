package com.match.honey.dialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.match.honey.R;
import com.match.honey.databinding.DialogPopupAdBinding;
import com.match.honey.utils.StringUtil;

public class DlgPopUpAd extends AppCompatActivity {
    DialogPopupAdBinding binding;
    AppCompatActivity act;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFinishOnTouchOutside(false);

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_popup_ad, null);
        act = this;


        String imageUrl = getIntent().getStringExtra("imageUrl");
        Log.e(StringUtil.TAG, "imageUrl: " + imageUrl);
        final String url = getIntent().getStringExtra("url");

        Glide.with(act).load(imageUrl).into(binding.ivContents);

        /* ok button listener */
        binding.llClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

//                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//                notificationManager.cancel(1);
            }
        });

        binding.ivContents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

//                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//                notificationManager.cancel(1);
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
