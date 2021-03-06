package com.match.honey.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.match.honey.R;
import com.match.honey.utils.StringUtil;


public class MoreimgActivity extends AppCompatActivity implements View.OnClickListener{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_picture);

        String img = getIntent().getStringExtra("imgurl");

        if(StringUtil.isNull(img)){
            Toast.makeText(this,"이미지를 불러올 수 없습니다.",Toast.LENGTH_SHORT).show();
        }else {

            PhotoView photoView = (PhotoView) findViewById(R.id.iv_moreimg);

            Glide.with(this)
                    .load(img)
                    .into(photoView);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.iv_close:
//                finish();
//                break;
        }
    }
}
