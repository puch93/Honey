package com.match.honey.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.match.honey.R;
import com.match.honey.utils.StringUtil;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class DlgAdviewFullScreen extends AppCompatActivity {

    private final String _P_FRONT = "front";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dlg_adview_fullscreen);

        String type = getIntent().getStringExtra("type");

        if (StringUtil.isNull(getIntent().getStringExtra("from"))) {
            AudioManager mAudioManger = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

            if (mAudioManger.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vib.vibrate(1000);
            }

            if (mAudioManger.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                Uri nofitication = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), nofitication);
                ringtone.play();
            }
        }

        final String target = getIntent().getStringExtra("targeturl");
        String imgurl = getIntent().getStringExtra("imgurl");
        Log.e(StringUtil.TAG, "imgurl: " + imgurl);

        ImageView adimg = (ImageView) findViewById(R.id.iv_adimg);
        LinearLayout close = (LinearLayout) findViewById(R.id.ll_adview_close);

        Glide.with(this)
                .load(imgurl)
                .into(adimg);


        adimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (URLUtil.isValidUrl(target)) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(target)));
                } else {
                    Toast.makeText(DlgAdviewFullScreen.this, "해당 사이트로 연결할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private Bitmap getBitmap(String url) {
        URL imgUrl = null;
        HttpURLConnection connection = null;
        InputStream is = null;

        Bitmap retBitmap = null;

        try {
            imgUrl = new URL(url);
            connection = (HttpURLConnection) imgUrl.openConnection();
            connection.setDoInput(true);
            connection.connect();
            is = connection.getInputStream();
            Bitmap tmp = BitmapFactory.decodeStream(is);
            retBitmap = Bitmap.createScaledBitmap(tmp, tmp.getWidth(), tmp.getHeight(), true);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            return retBitmap;
        }
    }
}
