package com.match.marryme.activity;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.MediaController;

import com.match.marryme.R;
import com.match.marryme.databinding.ActivityVideoviewBinding;
import com.match.marryme.utils.StringUtil;

public class VideoPlayAct extends Activity {
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
