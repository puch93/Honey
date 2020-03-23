package com.match.honey.activity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.match.honey.R;
import com.match.honey.databinding.ActivityVideoviewBinding;
import com.match.honey.utils.StringUtil;

public class VideoPlayAct extends AppCompatActivity {
    ActivityVideoviewBinding binding;

    String link;
    MediaController mediaController;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_videoview);
        link = getIntent().getStringExtra("link");

        if (StringUtil.isNull(link)){

        }else{

        }

        mediaController = new MediaController(this);

        Uri uri = Uri.parse(link);
        binding.videoArea.setVideoURI(uri);
        binding.videoArea.setMediaController(mediaController);
        binding.videoArea.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
